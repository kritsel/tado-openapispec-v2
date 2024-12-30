While on dev branch:
1. git merge main
2. update version number in .yaml
3. git add .
4. git commit -m "release version `<version number>`"
5. git push
6. git tag -a `<version number>` -m "release version `<version number>`"
7. git tag -af latest -m "latest=`<version number>`"
8. git push --force origin --tags
9. check if github job succeeds (executes integration test on the API)
10. git checkout main
11. git merge dev
12. git push
13. git checkout dev (to return to dev branch for future changes)
14. create release notes