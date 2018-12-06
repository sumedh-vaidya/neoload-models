# NeoLoad Extractors definition
Definition has several top-level keys.

| Name        | Description           |
| ----------- | --------------------- |
| Extractors  | List of extractors    |

You can find below an example of how to define extractors.

## Extractor
Extractors allow chaining several API calls where the argument of a call can be extracted from a previous call.

**Available settings are:**

| Name                   | Description                                                  | Required/Optional |
| ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------- |
| name                   | The name of the extractor                                                                                                                                                                                                                                                                                                                                                             | Required          |
| from                   | The response part where to extract value. The available values are "header", "body" and "both". The default value is "body".                                                                                                                                                                                                                                                          | Optional          |
| xpath                  | The XPath expression where to extract value. Only available when "from" value is "body".                                                                                                                                                                                                                                                                                                | Optional          |
| jsonpath               | The JSONPath expression where to extract value. Only available when "from" value is "body".                                                                                                                                                                                                                                                                                             | Optional          |
| regexp                 | The regular expression applies on "from" value. If "Body" and "xpath" or "jsonpath" is specified, then the extraction is done on the result of the xpath or jsonpath. The default value is "(.*)".                                                                                                                                                                                            | Optional          |
| match_number           | If multiple values have matched "regexp", the match number defines which match to use. Value "-1" is to extract all occurrences, "0" to randomly extract one occurence and "N" to extract the Nth occurrence. The default value is "1".                                                                                                                                                          | Optional          |
| template               | The template used to construct the value. This is an arbitrary string containing special elements that refer to groups within the regular expression (a group is a dynamic portion of the regular expression set within brackets): $0$ refers to the entire text matching the expression, $1$ refers to group 1, $2$ refers to group 2, and so on. The default value is $1$.          | Optional          |
| decode                 | The decoder to apply on the value. The available values are "html" and "url".                                                                                                                                                                                                                                                                                                         | Optional          |
| extract_once           | This option allows considering only the first value extracted. The available values are "true" and "false". The default value is "false".                                                                                                                                                                                                                                             | Optional          |
| default                | The value to return when not extracted. The default value is "<NOT FOUND>".                                                                                                                                                                                                                                                                                                           | Optional          |
| throw_assertion_error  | Raises an assertion error when no match found. The default value is "true".                                                                                                                                                                                                                                                                                                            | Optional          |


**Example:**

Defining 2 extractors: one  matching any number in header, and another one matching the first occurence of regular expression on JSONPath.

```yaml
extractors:
- name: any-number-on-header
  regexp: ([0-9]*)
- name: first-match-on-jsonpath
  jsonpath: $.features[0].type
  regexp: Fea(.*)
  match_number: 1
  decode: url
  extract_once: true
```