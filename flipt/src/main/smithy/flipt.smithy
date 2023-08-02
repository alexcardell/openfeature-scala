$version: "2"

namespace io.cardell.flipt.api

use smithy.api#http
use alloy#simpleRestJson

@simpleRestJson
service FliptApi {
  version: "0.1.0"
  errors: []
  operations: [
    GetName
  ]
}

@http(method: "GET", uri: "/example/{name}", code: 200)
@documentation("Create a template")
operation GetName {
  input: Input

  output: Unit
}

structure Input {
  @required
  @httpLabel
  name: String
}
