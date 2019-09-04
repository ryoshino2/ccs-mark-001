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
    stage('EC2'){
            sh 'scp -i /home/ryoshino/Downloads/ec2.pem /home/ryoshino/workspace/ccs-old-version/ccs-mark-001/target/ccsmark-0.0.1-SNAPSHOT.jar ubuntu@ec2-18-229-137-36.sa-east-1.compute.amazonaws.com:~/home/ubuntu/app'
    }
}