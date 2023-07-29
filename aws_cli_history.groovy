aws dynamodb create-table \
    --table-name Entities \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
        AttributeName=category,AttributeType=S \
        AttributeName=initiative,AttributeType=N \
        AttributeName=timeModified,AttributeType=N \
        AttributeName=alive,AttributeType=N \
    --key-schema \
        AttributeName=id,KeyType=HASH \
        AttributeName=category,KeyType=RANGE \
    --provisioned-throughput \
        ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --local-secondary-indexes \
        '[
            {
                "IndexName": "category-index",
                "KeySchema": [
                    {
                        "AttributeName": "id",
                        "KeyType": "HASH"
                    },
                    {
                        "AttributeName": "category",
                        "KeyType": "RANGE"
                    }
                ],
                "Projection": {
                    "ProjectionType": "ALL"
                }
            },
            {
                "IndexName": "initiative-index",
                "KeySchema": [
                    {
                        "AttributeName": "id",
                        "KeyType": "HASH"
                    },
                    {
                        "AttributeName": "initiative",
                        "KeyType": "RANGE"
                    }
                ],
                "Projection": {
                    "ProjectionType": "ALL"
                }
            },
            {
                "IndexName": "time-modified-index",
                "KeySchema": [
                    {
                        "AttributeName": "id",
                        "KeyType": "HASH"
                    },
                    {
                        "AttributeName": "timeModified",
                        "KeyType": "RANGE"
                    }
                ],
                "Projection": {
                    "ProjectionType": "ALL"
                }
            },
            {
                "IndexName": "alive-index",
                "KeySchema": [
                    {
                        "AttributeName": "id",
                        "KeyType": "HASH"
                    },
                    {
                        "AttributeName": "alive",
                        "KeyType": "RANGE"
                    }
                ],
                "Projection": {
                    "ProjectionType": "ALL"
                }
            }   
        ]'

aws dynamodb query \
    --table-name Entities \
    --key-condition-expression "id=:idValue" \
    --expression-attribute-values '{ ":idValue": {"S":"575f84c5-0561-46cf-85ae-70f270ab5bbb"} }' \
    --projection-expression "#id, #name" \
    --expression-attribute-names '{"#id" : "id", "#name":"name"}'\

aws dynamodb query \
    --table-name Entities \
    --key-condition-expression "id=:idValue AND timeModified>:timeModifiedValue" \
    --expression-attribute-values '{ ":timeModifiedValue":{"N":"0"}, ":idValue": {"S":"575f84c5-0561-46cf-85ae-70f270ab5bbb"} }' \
    --projection-expression "#id, #name" \
    --expression-attribute-names '{"#id" : "id", "#name":"name"}'\
    --limit 3

aws dynamodb query \
    --table-name Entities \
    --index-name "category-timeModified-index"\
    --key-condition-expression "category = :categoryValue" \
    --expression-attribute-values '{ ":categoryValue": {"S": "Player"} }' \
    --projection-expression "#id, #name" \
    --expression-attribute-names '{"#id" : "id", "#name":"name"}'\
    --limit 3
