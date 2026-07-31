pipeline {
    agent any

    environment {
        CI = 'true'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code...'
            }
        }

        stage('Build & Run Maven TestNG Tests') {
            steps {
                echo 'Executing Maven TestNG Automation Suite...'
                bat '''
                @echo off
                IF EXIST "mvnw.cmd" (
                    echo Found Maven Wrapper. Running via mvnw.cmd...
                    call mvnw.cmd clean test -DsuiteXmlFile=testng.xml -Dheadless=true
                ) ELSE (
                    echo Running via system mvn...
                    mvn clean test -DsuiteXmlFile=testng.xml -Dheadless=true
                )
                '''
            }
        }
    }

    post {
        always {
            echo 'Publishing TestNG & Surefire test results...'
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'

            echo 'Archiving test reports and screenshots...'
            archiveArtifacts artifacts: 'target/surefire-reports/**, test-results/screenshots/**', allowEmptyArchive: true
        }
        success {
            echo 'Build and TestNG automation suite completed successfully!'
        }
        failure {
            echo 'Build or TestNG automation suite failed. Check surefire reports for details.'
        }
    }
}
