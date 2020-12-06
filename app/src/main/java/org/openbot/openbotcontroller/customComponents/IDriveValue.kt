package org.openbot.openbotcontroller.customComponents

import kotlin.Float as DrivePositionAsFloatBetweenMinusOneAndOne

interface IDriveValue: (DrivePositionAsFloatBetweenMinusOneAndOne) -> DrivePositionAsFloatBetweenMinusOneAndOne {
    override operator fun invoke(x: DrivePositionAsFloatBetweenMinusOneAndOne): DrivePositionAsFloatBetweenMinusOneAndOne
}

