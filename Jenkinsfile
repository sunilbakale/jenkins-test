node {
    
		deleteDir()
		
			stage ('Clone') {
				checkout scm
			}
			stage ('Build') {
				
				sh ./gradlew build 
				}
			stage ('Tests') {
				parallel 'static': {
					sh "echo 'shell scripts to run static tests...'"
				},
				'unit': {
					sh "echo 'shell scripts to run unit tests...'"
				},
				'integration': {
					sh "echo 'shell scripts to run integration tests...'"
				}
			}
			stage ('Deploy') {
				sh "echo 'shell scripts to deploy to server...'"
			}
	}