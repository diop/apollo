package io.muun.apollo.domain.errors


import io.muun.apollo.data.external.UserFacingErrorMessages

class InvalidAddressError: UserFacingError(UserFacingErrorMessages.INSTANCE.invalidAddress())
