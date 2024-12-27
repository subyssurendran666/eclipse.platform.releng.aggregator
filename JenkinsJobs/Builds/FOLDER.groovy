def config = new groovy.json.JsonSlurper().parseText(readFileFromWorkspace('JenkinsJobs/JobDSL.json'))

folder('Builds') {
  description('Eclipse periodic build jobs.')
}

for (STREAM in config.Streams){

	pipelineJob('Builds/I-build-' + STREAM){
		description('Daily Eclipse Integration builds.')
		properties {
			pipelineTriggers {
				triggers {
					cron {
						spec('''TZ=America/Toronto
# format: Minute Hour Day Month Day of the week (0-7)

# - - - Integration Eclipse SDK builds - - - 
# 2025-03 Release Schedule
# Normal : 6 PM every day (1/6 - 2/9)
0 18 * * *


# Milestone/RC Schedule 
# Post M1, no nightlies, I-builds only. (Be sure to "turn off" for tests and sign off days)
# 0 6 14-26 2 5-7,1-3
# 0 18 14-26 2 5-7,1-3
''')
					}
				}
			}
		}
		definition {
			cpsScm {
				lightweight(true)
				scm {
					github('eclipse-platform/eclipse.platform.releng.aggregator', 'master')
				}
				scriptPath('JenkinsJobs/Builds/build.jenkinsfile')
			}
		}
	}

}

pipelineJob('Builds/Build-Docker-images'){
	description('Build and publish custom Docker images')
	properties {
		pipelineTriggers {
			triggers {
				cron { spec('@weekly') }
			}
		}
	}
	definition {
		cpsScm {
			lightweight(true)
			scm {
				github('eclipse-platform/eclipse.platform.releng.aggregator', 'master')
			}
			scriptPath('JenkinsJobs/Builds/DockerImagesBuild.jenkinsfile')
		}
	}
}
