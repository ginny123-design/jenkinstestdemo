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
                bat 'mvn clean test -DsuiteXmlFile=testng.xml'
            }
        }
    }

    post {
        always {
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
