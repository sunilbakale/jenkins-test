#!groovy

pipeline {

  def PROJECT_ID=cth-web-project-282111
  agent any

  environment {
    git_commit_message = ''
    git_commit_diff = ''
    git_commit_author = ''
    git_commit_author_name = ''
    git_commit_author_email = ''
  }

  stages {

    // Build
    stage('Build') {
      agent any
      steps {
        deleteDir()
        checkout scm	
		sh 'gradle build'
      }
    }

    // Static Code Analysis
    stage('Static Code Analysis') {
      agent any
      steps {
		sh '''
		docker build -t gcr.io/${PROJECT_ID}/cth-app:v15 .
		'''
	  }
    }

    // Unit Tests
    stage('Unit Tests') {
      agent any
      steps {
        sh "echo 'Run Unit Tests'"
		 sh '''
		docker push gcr.io/${PROJECT_ID}/cth-app:v15
		'''
      }
    }

    // Acceptance Tests
    stage('Acceptance Tests') {
      agent any
      steps {
        sh "echo 'Run Acceptance Tests'"
      }
    }
  }
  post {
    success {
      sh "echo 'Send mail on success'"
      // mail to:"me@example.com", subject:"SUCCESS: ${currentBuild.fullDisplayName}", body: "Yay, we passed."
    }
    failure {
      sh "echo 'Send mail on failure'"
      // mail to:"me@example.com", subject:"FAILURE: ${currentBuild.fullDisplayName}", body: "Boo, we failed."
    }
  }
}