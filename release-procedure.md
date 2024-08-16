1. update version number in .yaml
2. git add .
3. git commit -m "release version `<version number>`"
4. git push
4. git tag -a `<version number>` -m "release version <version number>"
5. git tag -af latest -m "release version latest=`<version number>`"
6. git push --force origin --tags
7. create release notes