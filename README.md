Jieba Analysis for ElasticSearch
================================

The Jieba Analysis plugin integrates Lucene / Jieba Analyzer into elasticsearch, support customized dictionary.


| Jieba Chinese Analysis Plugin | ElasticSearch | Analyzer       |
|-------------------------------|---------------|----------------|
| 0.0.2                         | 1.0.0RC2      | 0.0.2          |
| 0.0.3-SNAPSHOT                | 1.3.0         | 1.0.0          |
| 0.0.4                         | 1.5.x         | 1.0.2          |

The plugin includes the `jieba` analyzer, `jieba` tokenizer, and `jieba` token filter, and have two mode you can choose. one is `index` which means it will be used when you want to index a document. another is `search` mode which used when you want to search something.

Installation
------------

**compile and package current project**

```
git clone https://github.com/huaban/elasticsearch-analysis-jieba
cd elasticsearch-analysis-jieba
mvn package
```

**make a direcotry in elasticsearch' plugin directory**

```
cd {your_es_path}
mkdir plugins/jieba
```

**copy jieba-analysis-1.0.0.jar and elasticsearch-analysis-jieba-0.0.3-SNAPSHOT.jar to plugins/jieba**

```
cp ~/.m2/repository/com/huaban/jieba-analysis/1.0.0/jieba-analysis-1.0.0.jar {your_es_path}/plugins/jieba
cd elasticsearch-analysis-jieba
cp target/elasticsearch-analysis-jieba-0.0.3-SNAPSHOT.jar {your_es_path}/plugins/jieba
```

**copy user dict to config/jieba**

```
cp -r data/jieba {your_es_path}/config/
```

that's all!

Changelog
---------
2015.08.06, add another mode to support synonym.
```javascript
analyzer:
    jieba_index : 
        type : "jieba"
        seg_mode : "index"
        stop : true
    jieba_search : 
        type : "jieba"
        seg_mode : "search"
        stop : true
    jieba_synonym_index : 
        type : "jieba"
        seg_mode : "synonym_index"
        stop : true
```

```sh
# other mode 大小写全半角
curl '0:9200/test/_analyze?analyzer=jieba_synonym_index$pretty' -d '贝恩资本';echo
```

result

```javascript
{
  "tokens" : [ {
    "token" : "贝恩",
    "start_offset" : 0,
    "end_offset" : 2,
    "type" : "word",
    "position" : 1
  }, {
    "token" : "资本",
    "start_offset" : 2,
    "end_offset" : 4,
    "type" : "word",
    "position" : 2
  }, {
    "token" : "贝恩资本",
    "start_offset" : 0,
    "end_offset" : 4,
    "type" : "word",
    "position" : 3
  }, {
    "token" : "baincapital",
    "start_offset" : 0,
    "end_offset" : 11,
    "type" : "word",
    "position" : 4
  } ]
}
//as 贝恩资本 and baincapital are synonyms.
```

Add other mode. This mode don't split word, just doing some string conversion, case or full/half word

Usage
-----

create mapping

```sh
#!/bin/bash

curl -XDELETE '0:9200/test/';echo

curl -XPUT '0:9200/test/' -d '
{
    "index" : {
        "number_of_shards": 1,
        "number_of_replicas": 0,
        "analysis" : {
            "analyzer" : {
                "jieba_search" : {
                    "type" : "jieba",
                    "seg_mode" : "search",
                    "stop" : true
                },
                "jieba_other" : {
                    "type" : "jieba",
                    "seg_mode" : "other",
                    "stop" : true
                },
                "jieba_index" : {
                    "type" : "jieba",
                    "seg_mode" : "index",
                    "stop" : true
                }
            }
        }
    }
}';echo
```

test

```sh
# index mode
curl '0:9200/test/_analyze?analyzer=jieba_index' -d '中华人民共和国';echo
```

result

```javascript
{
    "tokens": [
        {
            "token": "中华",
            "start_offset": 0,
            "end_offset": 2,
            "type": "word",
            "position": 1
        },
        {
            "token": "华人",
            "start_offset": 1,
            "end_offset": 3,
            "type": "word",
            "position": 2
        },
        {
            "token": "人民",
            "start_offset": 2,
            "end_offset": 4,
            "type": "word",
            "position": 3
        },
        {
            "token": "共和",
            "start_offset": 4,
            "end_offset": 6,
            "type": "word",
            "position": 4
        },
        {
            "token": "共和国",
            "start_offset": 4,
            "end_offset": 7,
            "type": "word",
            "position": 5
        },
        {
            "token": "中华人民共和国",
            "start_offset": 0,
            "end_offset": 7,
            "type": "word",
            "position": 6
        }
    ]
}
```

```sh
# search mode
curl '0:9200/test/_analyze?analyzer=jieba_search' -d '中华人民共和国';echo
```

result

```javascript
{
    "tokens": [
        {
            "token": "中华人民共和国",
            "start_offset": 0,
            "end_offset": 7,
            "type": "word",
            "position": 1
        }
    ]
}
```

```sh
# other mode 大小写全半角
curl '0:9200/test/_analyze?analyzer=jieba_other' -d '中华人民共和国 HeLlo';echo
```

result

```javascript
{
    "tokens": [
        {
            "token": "中华人民共和国 hello",
            "start_offset": 0,
            "end_offset": 13,
            "type": "word",
            "position": 1
        }
    ]
}
```

License
-------

```
This software is licensed under the Apache 2 license, quoted below.

Copyright (C) 2013 libin and Huaban Inc<http://www.huaban.com>

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```
