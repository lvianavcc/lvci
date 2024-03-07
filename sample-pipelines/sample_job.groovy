pipelineJob('testJob2') {
    parameters {
        // booleanParam 'UPLOAD_RESULT', false, 'Set the `hpsdk.verified` property on the `baseline.json` file on Artifactory'

        // This pipeline depends on the `GERRIT_REFNAME` variable set by the
        // Gerrit Trigger plugin, which. When triggering manually we need to set
        // this variable ourselves.
        stringParam 'GERRIT_REFNAME', 'refs/heads/master', 'Script revision to use (default: refs/heads/master)'
    }
    properties {
        pipelineTriggers {
            [
            triggers {
                gerrit {
                    gerritProjects {
                        gerritProject {
                            branches {
                                branch {
                                    compareType 'REG_EXP'
                                    pattern '^refs/tags/v[0-9]+\\.[0-9]+\\.[0-9]+(\\+dev.*|rc[0-9]+)?$'
                                }
                            }
                            compareType 'PLAIN'
                            pattern 'myproj'
                            disableStrictForbiddenFileVerification false
                        }
                    }
                    triggerOnEvents {
                        refUpdated()
                    }
                }
            },
            triggers {
                gerrit {
                    gerritProjects {
                        gerritProject {
                            branches {
                                branch {
                                    compareType 'REG_EXP'
                                    pattern 'refs/heads/master'
                                }
                            }
                            compareType 'PLAIN'
                            pattern 'pipelines'
                            disableStrictForbiddenFileVerification false
                        }
                    }
                    triggerOnEvents {
                        changeMerged()
                    }
                }
            }
            ]
        }
    }
    definition {
        cpsScm {
            scm {
                git {
                    branch('master')
                    remote {
                        name('origin')
                        url('ssh://jenkins@gerrit:29418/pipelines.git')
                        credentials('gerrit-ssh-key')
                    }
                }
            }
            scriptPath('jobs/Jenkinsfile')
        }
    }
}
