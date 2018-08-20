node {
    def repoName = "service-statement-main"
    def artifactName = "${repoName}-0.1.${BUILD_NUMBER}.jar"
    def artifactPomName = "${repoName}-0.1.${BUILD_NUMBER}.pom"

    stage('scm') {
        dir(repoName) {
            git branch: 'master',
            credentialsId: 'mycredentials',
            url: 'http://feronti@bitbucket.viridian.cc/scm/stat/' + repoName + '.git'
        }
        sh('du -hcs *')
    }
    stage('Build') {
        echo "building " + artifactName
        dir(repoName) {
            sh "sed -i \"s/SNAPSHOT/${BUILD_NUMBER}/g\" pom.xml"
            sh "mvn -Dbuild.number=${BUILD_NUMBER} -DskipTests clean package"
        }
    }
    stage('test') {
         dir(repoName) {
             sh "mvn -Dbuild.number=${BUILD_NUMBER} -Dmaven.test.failure.ignore package"
             archiveArtifacts "target/" + artifactName
         }
         //junit '**/target/surefire-reports/TEST-*.xml'
    }
    stage("deploy") {

        def committerEmail = sh (
            script: 'git --no-pager show -s --format=\'%ae\'',
            returnStdout: true
        ).trim()

        def message = ' { "attachments": [ { '
            + ' "fallback": "Required plain-text summary of the attachment.", '
            + ' "color": "#36a64f", '
            + ' "author_name": "' + committerEmail + '", '
            + ' "title": "provider-corebank-0.1.41.jar", '
            + ' '"text": "has been built _successfully_, '
            + ' } ] } '

        slackSend message
        //slackSend color: 'good', message: ':computer: *' + artifactName + "* has been built _successfully_ \n"

        sh '/var/lib/jenkins/viridian/deploy-' + repoName + '.sh'

    }
}