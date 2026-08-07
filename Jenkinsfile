pipeline {
    agent any

    tools {
        // Names must match Manage Jenkins -> Tools configuration
        maven 'Maven3'
        jdk 'JDK17'
    }

    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox'], description: 'Browser to run tests on')
        string(name: 'SUITE_FILE', defaultValue: 'testng.xml', description: 'TestNG suite file to run')
    }

    environment {
        // headless is forced true on CI agents (no display)
        HEADLESS = 'true'
    }

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B clean compile'
            }
        }

        stage('Test') {
            steps {
                sh """
                    mvn -B test \
                        -Dbrowser=${params.BROWSER} \
                        -Dheadless=${env.HEADLESS} \
                        -DsuiteFile=${params.SUITE_FILE}
                """
            }
        }
    }

    post {
        always {
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
            archiveArtifacts artifacts: 'target/surefire-reports/**', allowEmptyArchive: true
        }
        failure {
            echo 'Build failed - check the surefire reports and console output above.'
        }
        success {
            echo 'All tests passed.'
        }
    }
}
