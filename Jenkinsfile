node {
   def mvnHome
   stage('Preparation') { // for display purposes
      git 'https://github.com/ryoshino2/ccs-mark-001'
   }
   stage('Clean') {
            sh './mvnw clean'
      }
      stage('Test') {
            sh './mvnw test'
      }
    stage('Build'){
            sh './mvnw package'
    }
}