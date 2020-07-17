
pipeline {

  agent {
    label ""
  }
  

  
  environment {
    // Environment variable identifiers need to be both valid bash variable
    // identifiers and valid Groovy variable identifiers. If you use an invalid
    // identifier, you'll get an error at validation time.
    // Right now, you can't do more complicated Groovy expressions or nesting of
    // other env vars in environment variable values, but that will be possible
    // when https://issues.jenkins-ci.org/browse/JENKINS-41748 is merged and
    // released.
    FOO = "BAR"
  }
  
  stages {
    // At least one stage is required.
    stage("first stage") {
      // Every stage must have a steps block containing at least one step.
      steps {
        // You can use steps that take another block of steps as an argument,
        // like this.
        //
        // But wait! Another validation issue! Two, actually! I didn't use the
        // right type for "time" and had a typo in "unit".
        timeout(time: true, uint: 'MINUTES') {
          echo "We're not doing anything particularly special here."
          echo "Just making sure that we don't take longer than five minutes"
          echo "Which, I guess, is kind of silly."
          
          // This'll output 3.3.3, since that's the Maven version we
          // configured above. Well, once we fix the validation error!
          sh "./gradlew build" 
        }
      }
      
      // Post can be used both on individual stages and for the entire build.
      post {
        success {
          echo "Only when we haven't failed running the first stage"
        }
        
        failure {
          echo "Only when we fail running the first stage."
        }
      }
    }

}