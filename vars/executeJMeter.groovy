/***************************\
  This function assumes we run on a Jenkins Agent that has JMeter installed

  Returns either 0(=no errors), 1(=func validation failed), 2(=response time validation failed)
\***************************/
def call(   final String scriptName, 
            final String serverUrl, 
            final int serverPort=80, 
            final String checkPath='/health', 
            final int vuCount=1, 
            final int loopCount=1, 
            final int thinkTime=250, 
            final String LTN='DTLoadTest', 
            final boolean funcValidation=false, 
            final int avgRtValidation=0, 
            final int retryOnError=0, 
            final int retryWait=5000)
{
    int errorCode = 0

    // lets run the test and put the console output to output.txt
    echo "Execute the jMeter test and console output goes to output.txt."
    sh "/jmeter/bin/jmeter.sh -n -t ./${scriptName} -e -o results -l result.tlf -JSERVER_URL='${serverUrl}' -JDT_LTN='${LTN}' -JVUCount='${vuCount}' -JLoopCount='${loopCount}' -JCHECK_PATH='${checkPath}' -JSERVER_PORT='${serverPort}' -JThinkTime='${thinkTime}' > output.txt"                    
    sh "cat output.txt"

    // archive the artifacts
    perfReport percentiles: '0,50,90,100', sourceDataFiles: 'result.tlf'
    archiveArtifacts artifacts:'*.*/**'

    // do post test validation checks
    sh "awk '/summary =/ {print \$15;}' output.txt >> errorCount.txt"
    def errorCount=readFile("errorCount.txt").trim()
    // DBG: echo "ErrorCount: ${errorCount}"

    if(funcValidation && errorCount > 0) {
        echo "More than 1 functional error"
        errorCode = 1
        return errorCode
    }

    sh "awk '/summary =/ {print \$9;}' output.txt >> avgRt.txt"
    def avgRt=readFile("avgRt.txt").trim()
    // DBG: echo "avgRt: ${avgRt}"

    if((avgRtValidation > 0) && (avgRt > avgRtValidation)) {
        echo "Response Time Threshold Violation: ${avgRt} > ${avgRtValidation}"
        errorCode = 2
        return errorCode
    }

    return errorCode
}