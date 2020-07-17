pipeline {

 
  agent any

  stages {

    stage('Checkout Source') {
      steps {
        checkout scm
      }
    }

    stage('Build image') {
      steps{
        script {
           sh ./gradlew  build
        }
      }
    }

    

    

  }

}