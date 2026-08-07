# Jenkins Plugin Hosting & Release Checklist

Use this after the code is on GitHub. Official guide:
https://www.jenkins.io/doc/developer/publishing/requesting-hosting/

## Prerequisites

- [x] Public GitHub repo: https://github.com/pushplus/perk-pushplus-plugin-jenkins
- [x] MIT `LICENSE` + `pom.xml` `<licenses>`
- [x] `accounts.jenkins.io` account: `pcstx` (log in once to Artifactory and Jira before RPU permissions)
- [x] GitHub user: `pcstx`

## 1. Open Hosting Request

Create an issue in:
https://github.com/jenkins-infra/repository-permissions-updater/issues/new/choose

Suggested field values:

| Field | Value |
|-------|-------|
| New Repository Name | `pushplus-plugin-jenkins` |
| GitHub Source Repository | `https://github.com/pushplus/perk-pushplus-plugin-jenkins` |
| Artifact ID | `pushplus-plugin-jenkins` |
| Group ID | `io.jenkins.plugins` |
| Description | Sends Jenkins build notifications to PushPlus (WeChat, webhook, email, SMS, etc.) |
| License | MIT |
| Jenkins account | `pcstx` |
| GitHub users to add | `pcstx` |

Issue title example:

```text
Hosting request: pushplus-plugin-jenkins
```

Issue body draft:

```markdown
### Plugin information

- **Plugin name**: PushPlus Notification
- **Artifact ID**: `pushplus-plugin-jenkins`
- **Group ID**: `io.jenkins.plugins`
- **Description**: Sends Jenkins build notifications to PushPlus push service (WeChat, webhook, email, SMS, etc.)
- **License**: MIT
- **Source repository**: https://github.com/pushplus/perk-pushplus-plugin-jenkins

### Accounts

- **Jenkins account**: pcstx
- **GitHub users who should have access**: pcstx

### Notes

This plugin is a `Notifier` / Pipeline step (`@Symbol("pushplus")`) for PushPlus notifications.
We would like the hosted repository name to be `pushplus-plugin-jenkins` to match the artifactId.
```

## 2. After fork into `jenkinsci`

1. Accept the `jenkinsci` org invitation.
2. Follow Hosting team instructions about deleting/re-forking so `jenkinsci/pushplus-plugin-jenkins` is the canonical root.
3. Update this repo's remotes to track `jenkinsci/pushplus-plugin-jenkins`.
4. In `pom.xml`, set:
   - `<gitHubRepo>jenkinsci/pushplus-plugin-jenkins</gitHubRepo>` (or `jenkinsci/${project.artifactId}`)
   - `<url>https://github.com/jenkinsci/pushplus-plugin-jenkins</url>`

## 3. Enable release permissions (JEP-229 CD recommended)

File a PR in `jenkins-infra/repository-permissions-updater` adding something like:

`permissions/plugin-pushplus-plugin-jenkins.yml`

```yaml
name: "pushplus-plugin-jenkins"
github: "jenkinsci/pushplus-plugin-jenkins"
cd:
  enabled: true
developers:
  - "pcstx"
```

After merge, wait until GitHub Actions secrets `MAVEN_USERNAME` and `MAVEN_TOKEN` appear on the jenkinsci repo.

## 4. First release

With CD enabled:

1. Ensure `Jenkinsfile` builds green on ci.jenkins.io.
2. Open a PR with a user-facing change (or documentation) and label it `enhancement` (or another release-worthy label).
3. Merge to the default branch; CD will publish to Artifactory.
4. Within a few hours the plugin appears on https://plugins.jenkins.io/pushplus-plugin-jenkins
5. Install via **Manage Jenkins > Plugins**, search **PushPlus Notification**.

## 5. Post-release polish

- Add plugin labels (e.g. `notifier`) via the Jenkins docs labeling process.
- Point README / badges at the live plugins.jenkins.io page.
- Keep Gitee as an optional mirror only; jenkinsci is the source of truth.
