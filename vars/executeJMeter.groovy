import org.apache.commons.io.FileUtils
/***************************\
  This function assumes we run on a Jenkins Agent that has JMeter installed

  Returns either 0(=no errors), 1(=func validation failed), 2(=response time validation failed)
\***************************/
def call( Map args ) 
    
    /*String scriptName, 
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
            int retryWait=5000*/
{
    // check input arguments
    String scriptName = args.containsKey("scriptName") ? args.scriptName : ""
    String resultsDir = args.containsKey("resultsDir") ? args.resultsDir : "results"
    String serverUrl = args.containsKey("serverUrl") ? args.serverUrl : ""
    int serverPort = args.containsKey("serverPort") ? args.serverPort : 80
    String checkPath = args.containsKey("checkPath") ? args.checkPath : "/health"
    int vuCount = args.containsKey("vuCount") ? args.vuCount : 1
    int loopCount = args.containsKey("loopCount") ? args.loopCount : 1
    int thinkTime = args.containsKey("thinkTime") ? args.thinkTime : 250
    String LTN = args.containsKey("LTN") ? args.LTN : "DTLoadTest"
    boolean funcValidation = args.containsKey("funcValidation") ? args.funcValidation : false
    int avgRtValidation = args.containsKey("avgRtValidation") ? args.avgRtValidation : 0
    int retryOnError = args.containsKey("retryOnError") ? args.retryOnError : 0
    int retryWait = args.containsKey("retryWait") ? args.retryWait : 5000

    // check minimum required params
    if(serverUrl == "" || scriptName == "") {
        echo "serverUrl and scriptName are mandatory parameters!"
        return -1
    }

    int errorCode = 0

    // check results dir exists and is empty
    def resultsFolder = new File(resultsDir)
    if (!resultsFolder.exists()) {
        echo "Creating results directory"
        resultsFolder.mkdirs()
    } else {
        echo "Clean results directory"
        FileUtils.cleanDirectory(resultsFolder)
    }

    // delete result.tlf if exists
    def resultsFile = new File('result.tlf')
    resultsFile.delete()

    sh "ls -l"

    // lets run the test and put the console output to output.txt
    echo "Execute the jMeter test and console output goes to output.txt."
    sh "/jmeter/bin/jmeter.sh -n -t ./${scriptName} -e -o ${resultsDir} -l result.tlf -JSERVER_URL='${serverUrl}' -JDT_LTN='${LTN}' -JVUCount='${vuCount}' -JLoopCount='${loopCount}' -JCHECK_PATH='${checkPath}' -JSERVER_PORT='${serverPort}' -JThinkTime='${thinkTime}' > output.txt"                    
    sh "cat output.txt"

    // archive the artifacts
    perfReport percentiles: '0,50,90,100', sourceDataFiles: 'result.tlf'
    // archiveArtifacts artifacts:'*.*/**'
    archiveArtifacts artifacts:"${resultsDir}/**"

    // do post test validation checks
    sh "awk '/summary =/ {print \$15;}' output.txt >> errorCount.txt"
    int errorCount=readFile("errorCount.txt").trim().toInteger()
    // DBG: echo "ErrorCount: ${errorCount}"

    if(funcValidation && errorCount > 0) {
        echo "More than 1 functional error"
        errorCode = 1
        return errorCode
    }

    sh "awk '/summary =/ {print \$9;}' output.txt >> avgRt.txt"
    int avgRt=readFile("avgRt.txt").trim().toInteger()
    // DBG: echo "avgRt: ${avgRt}"

    if((avgRtValidation > 0) && (avgRt > avgRtValidation)) {
        echo "Response Time Threshold Violation: ${avgRt} > ${avgRtValidation}"
        errorCode = 2
        return errorCode
    }

    return errorCode
}