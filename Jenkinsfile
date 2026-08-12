pipeline {
    agent any

    tools {
        // Names must match Manage Jenkins -> Tools configuration
        maven 'Maven3'
        jdk 'JDK17'
    }

    parameters {
      //  choice(name: 'BROWSER', choices: ['chrome', 'firefox'], description: 'Browser to run tests on')
      //  string(name: 'SUITE_FILE', defaultValue: 'testng.xml', description: 'TestNG suite file to run')
    choice(name: 'BROWSER', choices: ['chrome', 'firefox'], description: 'Browser to run tests on')
    string(name: 'SUITE_FILE', defaultValue: 'testng.xml', description: 'TestNG suite file to run')
    booleanParam(name: 'HEADLESS', defaultValue: false, description: 'Run browser headless (uncheck to watch it)')
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
                bat 'mvn -B clean compile'
            }
        }

        stage('Test') {
            steps {
                bat "mvn -B test -Dbrowser=${params.BROWSER} -Dheadless=${env.HEADLESS} -DsuiteFile=${params.SUITE_FILE}"
            }
        }
    }

    post {
        always {
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: false
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
