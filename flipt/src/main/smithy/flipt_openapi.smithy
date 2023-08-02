$version: "2.0"

namespace fliptapi

use alloy#simpleRestJson
use smithytranslate#contentType

@httpBearerAuth
@simpleRestJson
service FliptOpenapiService {
    operations: [
        AuthMethodK8SServiceVerifyServiceAccount
        AuthMethodOidcServiceAuthorizeURL
        AuthMethodOidcServiceCallback
        AuthMethodTokenServiceCreateToken
        AuthServiceDeleteToken
        AuthServiceExpireSelf
        AuthServiceGetSelf
        AuthServiceGetToken
        AuthServiceListTokens
        ConstraintsServiceCreate
        ConstraintsServiceDelete
        ConstraintsServiceUpdate
        EvaluateServiceBatchEvaluate
        EvaluateServiceEvaluate
        FlagsServiceCreate
        FlagsServiceDelete
        FlagsServiceGet
        FlagsServiceList
        FlagsServiceUpdate
        NamespacesServiceCreate
        NamespacesServiceDelete
        NamespacesServiceGet
        NamespacesServiceList
        NamespacesServiceUpdate
        SegmentsServiceCreate
        SegmentsServiceDelete
        SegmentsServiceGet
        SegmentsServiceList
        SegmentsServiceUpdate
        VariantsServiceCreate
        VariantsServiceDelete
        VariantsServiceUpdate
    ]
}

@auth([])
@http(
    method: "POST"
    uri: "/auth/v1/method/kubernetes/serviceaccount"
    code: 200
)
operation AuthMethodK8SServiceVerifyServiceAccount {
    input: AuthMethodK8SServiceVerifyServiceAccountInput
    output: AuthMethodK8SServiceVerifyServiceAccount200
}

@auth([])
@http(
    method: "GET"
    uri: "/auth/v1/method/oidc/{provider}/authorize"
    code: 200
)
operation AuthMethodOidcServiceAuthorizeURL {
    input: AuthMethodOidcServiceAuthorizeURLInput
    output: AuthMethodOidcServiceAuthorizeURL200
}

@auth([])
@http(
    method: "GET"
    uri: "/auth/v1/method/oidc/{provider}/callback"
    code: 200
)
operation AuthMethodOidcServiceCallback {
    input: AuthMethodOidcServiceCallbackInput
    output: AuthMethodOidcServiceCallback200
}

@auth([
    httpBearerAuth
])
@http(
    method: "POST"
    uri: "/auth/v1/method/token"
    code: 200
)
operation AuthMethodTokenServiceCreateToken {
    input: AuthMethodTokenServiceCreateTokenInput
    output: AuthMethodTokenServiceCreateToken200
}

@auth([
    httpBearerAuth
])
@http(
    method: "DELETE"
    uri: "/auth/v1/tokens/{id}"
    code: 200
)
operation AuthServiceDeleteToken {
    input: AuthServiceDeleteTokenInput
    output: Unit
}

@auth([
    httpBearerAuth
])
@http(
    method: "PUT"
    uri: "/auth/v1/self/expire"
    code: 200
)
operation AuthServiceExpireSelf {
    input: AuthServiceExpireSelfInput
    output: Unit
}

@auth([
    httpBearerAuth
])
@http(
    method: "GET"
    uri: "/auth/v1/self"
    code: 200
)
operation AuthServiceGetSelf {
    input: Unit
    output: AuthServiceGetSelf200
}

@auth([
    httpBearerAuth
])
@http(
    method: "GET"
    uri: "/auth/v1/tokens/{id}"
    code: 200
)
operation AuthServiceGetToken {
    input: AuthServiceGetTokenInput
    output: AuthServiceGetToken200
}

@auth([
    httpBearerAuth
])
@http(
    method: "GET"
    uri: "/auth/v1/tokens"
    code: 200
)
operation AuthServiceListTokens {
    input: Unit
    output: AuthServiceListTokens200
}

@auth([
    httpBearerAuth
])
@http(
    method: "POST"
    uri: "/api/v1/namespaces/{namespaceKey}/segments/{segmentKey}/constraints"
    code: 200
)
operation ConstraintsServiceCreate {
    input: ConstraintsServiceCreateInput
    output: ConstraintsServiceCreate200
}

@auth([
    httpBearerAuth
])
@http(
    method: "DELETE"
    uri: "/api/v1/namespaces/{namespaceKey}/segments/{segmentKey}/constraints/{id}"
    code: 200
)
operation ConstraintsServiceDelete {
    input: ConstraintsServiceDeleteInput
    output: Unit
}

@auth([
    httpBearerAuth
])
@http(
    method: "PUT"
    uri: "/api/v1/namespaces/{namespaceKey}/segments/{segmentKey}/constraints/{id}"
    code: 200
)
operation ConstraintsServiceUpdate {
    input: ConstraintsServiceUpdateInput
    output: Unit
}

@auth([
    httpBearerAuth
])
@http(
    method: "POST"
    uri: "/api/v1/namespaces/{namespaceKey}/batch-evaluate"
    code: 200
)
operation EvaluateServiceBatchEvaluate {
    input: EvaluateServiceBatchEvaluateInput
    output: EvaluateServiceBatchEvaluate200
}

@auth([
    httpBearerAuth
])
@http(
    method: "POST"
    uri: "/api/v1/namespaces/{namespaceKey}/evaluate"
    code: 200
)
operation EvaluateServiceEvaluate {
    input: EvaluateServiceEvaluateInput
    output: EvaluateServiceEvaluate200
}

@auth([
    httpBearerAuth
])
@http(
    method: "POST"
    uri: "/api/v1/namespaces/{namespaceKey}/flags"
    code: 200
)
operation FlagsServiceCreate {
    input: FlagsServiceCreateInput
    output: FlagsServiceCreate200
}

@auth([
    httpBearerAuth
])
@http(
    method: "DELETE"
    uri: "/api/v1/namespaces/{namespaceKey}/flags/{key}"
    code: 200
)
operation FlagsServiceDelete {
    input: FlagsServiceDeleteInput
    output: Unit
}

@auth([
    httpBearerAuth
])
@http(
    method: "GET"
    uri: "/api/v1/namespaces/{namespaceKey}/flags/{key}"
    code: 200
)
operation FlagsServiceGet {
    input: FlagsServiceGetInput
    output: FlagsServiceGet200
}

@auth([
    httpBearerAuth
])
@http(
    method: "GET"
    uri: "/api/v1/namespaces/{namespaceKey}/flags"
    code: 200
)
operation FlagsServiceList {
    input: FlagsServiceListInput
    output: FlagsServiceList200
}

@auth([
    httpBearerAuth
])
@http(
    method: "PUT"
    uri: "/api/v1/namespaces/{namespaceKey}/flags/{key}"
    code: 200
)
operation FlagsServiceUpdate {
    input: FlagsServiceUpdateInput
    output: FlagsServiceUpdate200
}

@auth([
    httpBearerAuth
])
@http(
    method: "POST"
    uri: "/api/v1/namespaces"
    code: 200
)
operation NamespacesServiceCreate {
    input: NamespacesServiceCreateInput
    output: NamespacesServiceCreate200
}

@auth([
    httpBearerAuth
])
@http(
    method: "DELETE"
    uri: "/api/v1/namespaces/{key}"
    code: 200
)
operation NamespacesServiceDelete {
    input: NamespacesServiceDeleteInput
    output: Unit
}

@auth([
    httpBearerAuth
])
@http(
    method: "GET"
    uri: "/api/v1/namespaces/{key}"
    code: 200
)
operation NamespacesServiceGet {
    input: NamespacesServiceGetInput
    output: NamespacesServiceGet200
}

@auth([
    httpBearerAuth
])
@http(
    method: "GET"
    uri: "/api/v1/namespaces"
    code: 200
)
operation NamespacesServiceList {
    input: NamespacesServiceListInput
    output: NamespacesServiceList200
}

@auth([
    httpBearerAuth
])
@http(
    method: "PUT"
    uri: "/api/v1/namespaces/{key}"
    code: 200
)
operation NamespacesServiceUpdate {
    input: NamespacesServiceUpdateInput
    output: NamespacesServiceUpdate200
}

@auth([
    httpBearerAuth
])
@http(
    method: "POST"
    uri: "/api/v1/namespaces/{namespaceKey}/segments"
    code: 200
)
operation SegmentsServiceCreate {
    input: SegmentsServiceCreateInput
    output: SegmentsServiceCreate200
}

@auth([
    httpBearerAuth
])
@http(
    method: "DELETE"
    uri: "/api/v1/namespaces/{namespaceKey}/segments/{key}"
    code: 200
)
operation SegmentsServiceDelete {
    input: SegmentsServiceDeleteInput
    output: Unit
}

@auth([
    httpBearerAuth
])
@http(
    method: "GET"
    uri: "/api/v1/namespaces/{namespaceKey}/segments/{key}"
    code: 200
)
operation SegmentsServiceGet {
    input: SegmentsServiceGetInput
    output: SegmentsServiceGet200
}

@auth([
    httpBearerAuth
])
@http(
    method: "GET"
    uri: "/api/v1/namespaces/{namespaceKey}/segments"
    code: 200
)
operation SegmentsServiceList {
    input: SegmentsServiceListInput
    output: SegmentsServiceList200
}

@auth([
    httpBearerAuth
])
@http(
    method: "PUT"
    uri: "/api/v1/namespaces/{namespaceKey}/segments/{key}"
    code: 200
)
operation SegmentsServiceUpdate {
    input: SegmentsServiceUpdateInput
    output: SegmentsServiceUpdate200
}

@auth([
    httpBearerAuth
])
@http(
    method: "POST"
    uri: "/api/v1/namespaces/{namespaceKey}/flags/{flagKey}/variants"
    code: 200
)
operation VariantsServiceCreate {
    input: VariantsServiceCreateInput
    output: VariantsServiceCreate200
}

@auth([
    httpBearerAuth
])
@http(
    method: "DELETE"
    uri: "/api/v1/namespaces/{namespaceKey}/flags/{flagKey}/variants/{id}"
    code: 200
)
operation VariantsServiceDelete {
    input: VariantsServiceDeleteInput
    output: Unit
}

@auth([
    httpBearerAuth
])
@http(
    method: "PUT"
    uri: "/api/v1/namespaces/{namespaceKey}/flags/{flagKey}/variants/{id}"
    code: 200
)
operation VariantsServiceUpdate {
    input: VariantsServiceUpdateInput
    output: VariantsServiceUpdate200
}

structure Authauthentication {
    @required
    id: String
    @required
    method: AuthauthenticationMethod
    @required
    @timestampFormat("date-time")
    createdAt: Timestamp
    @required
    @timestampFormat("date-time")
    updatedAt: Timestamp
    @timestampFormat("date-time")
    expiresAt: Timestamp
    @required
    metadata: Metadata
}

structure AuthauthenticationList {
    @required
    authentications: Authentications
    @required
    nextPageToken: String
}

structure AuthauthenticationToken {
    @required
    clientToken: String
    @required
    authentication: Authauthentication
}

structure AuthMethodK8SServiceVerifyServiceAccount200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: AuthauthenticationToken
}

structure AuthMethodK8SServiceVerifyServiceAccountInput {
    @httpPayload
    @required
    @contentType("application/json")
    body: AuthMethodK8SServiceVerifyServiceAccountInputBody
}

structure AuthMethodK8SServiceVerifyServiceAccountInputBody {
    @required
    serviceAccountToken: String
}

structure AuthMethodOidcoidcAuthorizeURLResponse {
    @required
    authorizeUrl: String
}

structure AuthMethodOidcoidcCallbackResponse {
    @required
    authentication: Authauthentication
}

structure AuthMethodOidcServiceAuthorizeURL200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: AuthMethodOidcoidcAuthorizeURLResponse
}

structure AuthMethodOidcServiceAuthorizeURLInput {
    @httpLabel
    @required
    provider: String
    @httpQuery("state")
    @required
    state: String
}

structure AuthMethodOidcServiceCallback200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: AuthMethodOidcoidcCallbackResponse
}

structure AuthMethodOidcServiceCallbackInput {
    @httpLabel
    @required
    provider: String
    @httpQuery("code")
    @required
    code: String
    @httpQuery("state")
    @required
    state: String
}

structure AuthMethodTokenServiceCreateToken200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: AuthauthenticationToken
}

structure AuthMethodTokenServiceCreateTokenInput {
    @httpPayload
    @required
    @contentType("application/json")
    body: AuthMethodTokenServiceCreateTokenInputBody
}

structure AuthMethodTokenServiceCreateTokenInputBody {
    @required
    name: String
    @required
    description: String
    @timestampFormat("date-time")
    expiresAt: Timestamp
}

structure AuthServiceDeleteTokenInput {
    @httpLabel
    @required
    id: String
}

structure AuthServiceExpireSelfInput {
    @httpPayload
    @required
    @contentType("application/json")
    body: AuthServiceExpireSelfInputBody
}

structure AuthServiceExpireSelfInputBody {
    @timestampFormat("date-time")
    expiresAt: Timestamp
}

structure AuthServiceGetSelf200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: Authauthentication
}

structure AuthServiceGetToken200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: Authauthentication
}

structure AuthServiceGetTokenInput {
    @httpLabel
    @required
    id: String
}

structure AuthServiceListTokens200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: AuthauthenticationList
}

structure CommonsPageable {
    nextPageToken: String
    totalCount: Integer
}

structure Constraintsconstraint {
    @required
    id: String
    @required
    segmentKey: String
    @required
    type: ConstraintsconstraintComparisonType
    @required
    property: String
    @required
    operator: String
    @required
    value: String
    @required
    description: String
    @required
    @timestampFormat("date-time")
    createdAt: Timestamp
    @required
    @timestampFormat("date-time")
    updatedAt: Timestamp
}

structure ConstraintsconstraintCreateRequest {
    @required
    type: ConstraintsconstraintComparisonType
    @required
    property: String
    @required
    operator: String
    value: String
    description: String
}

structure ConstraintsconstraintUpdateRequest {
    @required
    type: ConstraintsconstraintComparisonType
    @required
    property: String
    @required
    operator: String
    value: String
    description: String
}

structure ConstraintsServiceCreate200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: Constraintsconstraint
}

structure ConstraintsServiceCreateInput {
    @httpLabel
    @required
    namespaceKey: String
    @httpLabel
    @required
    segmentKey: String
    @httpPayload
    @required
    @contentType("application/json")
    body: ConstraintsconstraintCreateRequest
}

structure ConstraintsServiceDeleteInput {
    @httpLabel
    @required
    namespaceKey: String
    @httpLabel
    @required
    segmentKey: String
    @httpLabel
    @required
    id: String
}

structure ConstraintsServiceUpdateInput {
    @httpLabel
    @required
    namespaceKey: String
    @httpLabel
    @required
    segmentKey: String
    @httpLabel
    @required
    id: String
    @httpPayload
    @required
    @contentType("application/json")
    body: ConstraintsconstraintUpdateRequest
}

structure Distributionsdistribution {
    @required
    id: String
    @required
    ruleId: String
    @required
    variantId: String
    @required
    rollout: Double
    @required
    @timestampFormat("date-time")
    createdAt: Timestamp
    @required
    @timestampFormat("date-time")
    updatedAt: Timestamp
}

structure DistributionsdistributionCreateRequest {
    @required
    variantId: String
    @required
    rollout: Double
}

structure DistributionsdistributionUpdateRequest {
    @required
    variantId: String
    @required
    rollout: Double
}

structure EvaluatebatchEvaluationRequest {
    requestId: String
    @required
    requests: Requests
    excludeNotFound: Boolean
}

structure EvaluatebatchEvaluationResponse {
    @required
    requestId: String
    @required
    responses: Responses
    @required
    requestDurationMillis: Double
}

structure EvaluateevaluationRequest {
    requestId: String
    @required
    flagKey: String
    @required
    entityId: String
    @required
    context: Context
}

structure EvaluateevaluationResponse {
    @required
    requestId: String
    @required
    entityId: String
    @required
    requestContext: RequestContext
    @required
    match: Boolean
    @required
    flagKey: String
    @required
    segmentKey: String
    @required
    @timestampFormat("date-time")
    timestamp: Timestamp
    @required
    value: String
    @required
    requestDurationMillis: Double
    @required
    attachment: String
    @required
    reason: EvaluateevaluationReason
}

structure EvaluateServiceBatchEvaluate200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: EvaluatebatchEvaluationResponse
}

structure EvaluateServiceBatchEvaluateInput {
    @httpLabel
    @required
    namespaceKey: String
    @httpPayload
    @required
    @contentType("application/json")
    body: EvaluatebatchEvaluationRequest
}

structure EvaluateServiceEvaluate200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: EvaluateevaluationResponse
}

structure EvaluateServiceEvaluateInput {
    @httpLabel
    @required
    namespaceKey: String
    @httpPayload
    @required
    @contentType("application/json")
    body: EvaluateevaluationRequest
}

structure Flagsflag {
    @required
    key: String
    @required
    name: String
    @required
    description: String
    @required
    enabled: Boolean
    @required
    @timestampFormat("date-time")
    createdAt: Timestamp
    @required
    @timestampFormat("date-time")
    updatedAt: Timestamp
    @required
    variants: Variants
}

structure FlagsflagCreateRequest {
    @required
    key: String
    @required
    name: String
    description: String
    enabled: Boolean
}

structure FlagsflagList {
    @required
    flags: Flags
    @required
    nextPageToken: String
    @required
    totalCount: Integer
}

structure FlagsflagUpdateRequest {
    @required
    name: String
    description: String
    enabled: Boolean
}

structure FlagsServiceCreate200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: Flagsflag
}

structure FlagsServiceCreateInput {
    @httpLabel
    @required
    namespaceKey: String
    @httpPayload
    @required
    @contentType("application/json")
    body: FlagsflagCreateRequest
}

structure FlagsServiceDeleteInput {
    @httpLabel
    @required
    namespaceKey: String
    @httpLabel
    @required
    key: String
}

structure FlagsServiceGet200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: Flagsflag
}

structure FlagsServiceGetInput {
    @httpLabel
    @required
    namespaceKey: String
    @httpLabel
    @required
    key: String
}

structure FlagsServiceList200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: FlagsflagList
}

structure FlagsServiceListInput {
    @httpLabel
    @required
    namespaceKey: String
    @httpQuery("limit")
    limit: Integer
    @httpQuery("offset")
    offset: Integer
    @httpQuery("pageToken")
    pageToken: String
}

structure FlagsServiceUpdate200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: Flagsflag
}

structure FlagsServiceUpdateInput {
    @httpLabel
    @required
    namespaceKey: String
    @httpLabel
    @required
    key: String
    @httpPayload
    @required
    @contentType("application/json")
    body: FlagsflagUpdateRequest
}

structure Namespacesnamespace {
    @required
    key: String
    @required
    name: String
    @required
    description: String
    @required
    protected: Boolean
    @required
    @timestampFormat("date-time")
    createdAt: Timestamp
    @required
    @timestampFormat("date-time")
    updatedAt: Timestamp
}

structure NamespacesnamespaceCreateRequest {
    @required
    key: String
    @required
    name: String
    description: String
}

structure NamespacesnamespaceList {
    @required
    namespaces: Namespaces
    @required
    nextPageToken: String
    @required
    totalCount: Integer
}

structure NamespacesnamespaceUpdateRequest {
    @required
    name: String
    description: String
}

structure NamespacesServiceCreate200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: Namespacesnamespace
}

structure NamespacesServiceCreateInput {
    @httpPayload
    @required
    @contentType("application/json")
    body: NamespacesnamespaceCreateRequest
}

structure NamespacesServiceDeleteInput {
    @httpLabel
    @required
    key: String
}

structure NamespacesServiceGet200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: Namespacesnamespace
}

structure NamespacesServiceGetInput {
    @httpLabel
    @required
    key: String
}

structure NamespacesServiceList200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: NamespacesnamespaceList
}

structure NamespacesServiceListInput {
    @httpQuery("limit")
    limit: Integer
    @httpQuery("offset")
    offset: Integer
    @httpQuery("pageToken")
    pageToken: String
}

structure NamespacesServiceUpdate200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: Namespacesnamespace
}

structure NamespacesServiceUpdateInput {
    @httpLabel
    @required
    key: String
    @httpPayload
    @required
    @contentType("application/json")
    body: NamespacesnamespaceUpdateRequest
}

structure Rulesrule {
    @required
    id: String
    @required
    flagKey: String
    @required
    segmentKey: String
    @required
    distributions: Distributions
    @required
    rank: Integer
    @required
    @timestampFormat("date-time")
    createdAt: Timestamp
    @required
    @timestampFormat("date-time")
    updatedAt: Timestamp
}

structure RulesruleCreateRequest {
    @required
    segmentKey: String
    @required
    rank: Integer
}

structure RulesruleList {
    @required
    rules: Rules
    @required
    nextPageToken: String
    @required
    totalCount: Integer
}

structure RulesruleOrderRequest {
    @required
    ruleIds: RuleIds
}

structure RulesruleUpdateRequest {
    @required
    segmentKey: String
}

structure Segmentssegment {
    @required
    key: String
    @required
    name: String
    @required
    description: String
    @required
    @timestampFormat("date-time")
    createdAt: Timestamp
    @required
    @timestampFormat("date-time")
    updatedAt: Timestamp
    @required
    constraints: Constraints
    @required
    matchType: SegmentssegmentMatchType
}

structure SegmentssegmentCreateRequest {
    @required
    key: String
    @required
    name: String
    @required
    description: String
    @required
    matchType: SegmentssegmentMatchType
}

structure SegmentssegmentList {
    @required
    segments: Segments
    @required
    nextPageToken: String
    @required
    totalCount: Integer
}

structure SegmentssegmentUpdateRequest {
    @required
    name: String
    @required
    description: String
    @required
    matchType: SegmentssegmentMatchType
}

structure SegmentsServiceCreate200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: Segmentssegment
}

structure SegmentsServiceCreateInput {
    @httpLabel
    @required
    namespaceKey: String
    @httpPayload
    @required
    @contentType("application/json")
    body: SegmentssegmentCreateRequest
}

structure SegmentsServiceDeleteInput {
    @httpLabel
    @required
    namespaceKey: String
    @httpLabel
    @required
    key: String
}

structure SegmentsServiceGet200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: Segmentssegment
}

structure SegmentsServiceGetInput {
    @httpLabel
    @required
    namespaceKey: String
    @httpLabel
    @required
    key: String
}

structure SegmentsServiceList200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: SegmentssegmentList
}

structure SegmentsServiceListInput {
    @httpLabel
    @required
    namespaceKey: String
    @httpQuery("limit")
    limit: Integer
    @httpQuery("offset")
    offset: Integer
    @httpQuery("pageToken")
    pageToken: String
}

structure SegmentsServiceUpdate200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: Segmentssegment
}

structure SegmentsServiceUpdateInput {
    @httpLabel
    @required
    namespaceKey: String
    @httpLabel
    @required
    key: String
    @httpPayload
    @required
    @contentType("application/json")
    body: SegmentssegmentUpdateRequest
}

structure VariantsServiceCreate200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: Variantsvariant
}

structure VariantsServiceCreateInput {
    @httpLabel
    @required
    namespaceKey: String
    @httpLabel
    @required
    flagKey: String
    @httpPayload
    @required
    @contentType("application/json")
    body: VariantsvariantCreateRequest
}

structure VariantsServiceDeleteInput {
    @httpLabel
    @required
    namespaceKey: String
    @httpLabel
    @required
    flagKey: String
    @httpLabel
    @required
    id: String
}

structure VariantsServiceUpdate200 {
    @httpPayload
    @required
    @contentType("application/json")
    body: Variantsvariant
}

structure VariantsServiceUpdateInput {
    @httpLabel
    @required
    namespaceKey: String
    @httpLabel
    @required
    flagKey: String
    @httpLabel
    @required
    id: String
    @httpPayload
    @required
    @contentType("application/json")
    body: VariantsvariantUpdateRequest
}

structure Variantsvariant {
    @required
    id: String
    @required
    flagKey: String
    @required
    key: String
    @required
    name: String
    @required
    description: String
    @required
    @timestampFormat("date-time")
    createdAt: Timestamp
    @required
    @timestampFormat("date-time")
    updatedAt: Timestamp
    @required
    attachment: String
}

structure VariantsvariantCreateRequest {
    @required
    key: String
    name: String
    description: String
    attachment: String
}

structure VariantsvariantUpdateRequest {
    @required
    key: String
    name: String
    description: String
    attachment: String
}

list Authentications {
    member: Authauthentication
}

list Constraints {
    member: Constraintsconstraint
}

list Distributions {
    member: Distributionsdistribution
}

list Flags {
    member: Flagsflag
}

list Namespaces {
    member: Namespacesnamespace
}

list Requests {
    member: EvaluateevaluationRequest
}

list Responses {
    member: EvaluateevaluationResponse
}

list RuleIds {
    member: String
}

list Rules {
    member: Rulesrule
}

list Segments {
    member: Segmentssegment
}

list Variants {
    member: Variantsvariant
}

map Context {
    key: String
    value: String
}

map Metadata {
    key: String
    value: String
}

map RequestContext {
    key: String
    value: String
}

/// The default is METHOD_NONE
enum AuthauthenticationMethod {
    METHOD_NONE
    METHOD_TOKEN
    METHOD_OIDC
    METHOD_KUBERNETES
}

/// The default is UNKNOWN_COMPARISON_TYPE
enum ConstraintsconstraintComparisonType {
    UNKNOWN_COMPARISON_TYPE
    STRING_COMPARISON_TYPE
    NUMBER_COMPARISON_TYPE
    BOOLEAN_COMPARISON_TYPE
    DATETIME_COMPARISON_TYPE
}

enum EvaluateevaluationReason {
    UNKNOWN_EVALUATION_REASON
    FLAG_DISABLED_EVALUATION_REASON
    FLAG_NOT_FOUND_EVALUATION_REASON
    MATCH_EVALUATION_REASON
    ERROR_EVALUATION_REASON
}

enum SegmentssegmentMatchType {
    ALL_MATCH_TYPE
    ANY_MATCH_TYPE
}
