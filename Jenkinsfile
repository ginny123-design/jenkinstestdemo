pipeline {
    agent any

    environment {
        CI = 'true'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code...'
                // Code is checked out automatically by Jenkins SCMS/Workspace
            }
        }

        stage('Install Dependencies') {
            steps {
                echo 'Installing Node.js dependencies...'
                bat 'npm install'
                echo 'Installing Playwright browsers...'
                bat 'npx playwright install chromium'
            }
        }

        stage('Run Tests') {
            steps {
                echo 'Running Playwright Automation Tests...'
                bat 'npx playwright test'
            }
        }
    }

    post {
        always {
            echo 'Archiving test results...'
            archiveArtifacts artifacts: 'playwright-report/**, test-results/**', allowEmptyArchive: true
        }
        success {
            echo 'Build and automation tests completed successfully!'
        }
        failure {
            echo 'Build or automation tests failed. Check logs for details.'
        }
    }
}
