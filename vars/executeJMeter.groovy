/***************************\
  This function assumes we run on a Jenkins Agent that has JMeter installed

  Returns either 0(=no errors), 1(=func validation failed), 2(=response time validation failed)
\***************************/
def call(   String scriptName, 
            String serverUrl, 
            int serverPort=80, 
            String checkPath='/health', 
            int vuCount=1, 
            int loopCount=1, 
            int thinkTime=250, 
            String LTN='DTLoadTest', 
            boolean funcValidation=false, 
            int avgRtValidation=0, 
            int retryOnError=0, 
            int retryWait=5000)
{
    int errorCode = 0

    // lets run the test and put the console output to output.txt
    sh "echo 'execute the jmeter test and console output goes to output.txt'"
    sh "/jmeter/bin/jmeter.sh -n -t ./${scriptName} -e -o results -l result.tlf -JSERVER_URL='${serverUrl}' -JDT_LTN='${LTN}' -JVUCount='${vuCount}' -JLoopCount='${loopCount}' -JCHECK_PATH='${checkPath}' -JSERVER_PORT='${serverPort}' -JThinkTime='${thinkTime}' > output.txt"                    
    sh "cat output.txt"

    // archive the artifacts
    perfReport percentiles: '0,50,90,100', sourceDataFiles: 'result.tlf'
    archiveArtifacts artifacts:'*.*/**'

    // do post test validation checks
    sh "awk '/summary =/ {print \$15;}' output.txt >> errorCount.txt"
    def errorCount=readFile("errorCount.txt").trim()
    if(funcValidation && errorCount > 0) {
        echo "More than 1 functional error"
        errorCode = 1
        return errorCode
    }

    sh "awk '/summary =/ {print \$9;}' output.txt >> avgRt.txt"
    def avgRt=readFile("avgRt.txt").trim()
    if((avgRtValidation > 0) && (avgRt > avgRtValidation)) {
        echo "Response Time Threshold Violation: ${avgRt} > ${avgRtValidation}"
        errorCode = 2
        return errorCode
    }

    return errorCode
}