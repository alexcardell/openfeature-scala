# `flipt`

This module provides a Flipt backend, calling Flipt's API -- no SDK.

This is accomplished by:

- Taking the Flipt OpenAPI specification
- Converting it to a Smithy spec using `smithytranslate` (with some removing of unimportant endpoints to please the Smithy compiler)
- Using `smithy4s` to generate the API client
