node {
	stage('SCM') {
		checkout([$class: 'GitSCM', branches: [[name: '*/develop/WDPH_M_20180906']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CleanBeforeCheckout']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '1a5bdf2d-3aa7-4dbc-95ae-77d4de324690', url: 'git@10.53.144.172:apigateway-group/wdph-filetransfer.git']]])
	}

	stage('Code Quality') {
		//sh 'mvn sonar:sonar'
	}

	stage('Packaging') {
		sh '/data/maven/bin/mvn clean package -DskipTests -U'
		archiveArtifacts 'target/*.jar'
	}

	stage('Build Images') {
		//sh 'bash build_and_push.sh'
	}

	stage('Deployment') {
		sh '/usr/bin/ansible-playbook -i playbook/test playbook/site.yml'
	}
}