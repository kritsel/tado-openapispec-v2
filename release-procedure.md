While on dev branch:
1. git merge main
2. update version number in .yaml
3. git add .
4. git commit -m "release version `<version number>`"
5. git push
6. git tag -a `<version number>` -m "release version <version number>"
7. git tag -af latest -m "release version latest=`<version number>`"
8. git push --force origin --tags
9. git checkout main
10. git merge dev
11. git checkout dev (to return to dev branch for future changes)
10. create release notes