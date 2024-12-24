FILE=tado-openapispec-v2.yaml
LATEST_TAG_COMMIT=$(git rev-list -n 1 latest)
FILE_COMMIT=$(git log -n 1 --pretty=format:%H -- $FILE)
if [ "$LATEST_TAG_COMMIT" = "$FILE_COMMIT" ]; then
  echo "$FILE is at the 'latest' tag."
else
  echo "$FILE is NOT at the 'latest' tag."
fi