pipeline {
    agent any
    tools {
        jdk 'JDK25'
        maven 'Maven3'
    }
    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'edge', 'firefox'], description: 'Browser to run')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run browser headlessly')
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Test') {
            steps {
                sh "mvn --batch-mode clean test -Dbrowser=${params.BROWSER} -Dheadless=${params.HEADLESS}"
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: 'reports/**,logs/**,target/surefire-reports/**',
                    allowEmptyArchive: true
            junit testResults: 'target/surefire-reports/TEST-*.xml', allowEmptyResults: true
        }
    }
}
