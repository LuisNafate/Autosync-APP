package com.autosync.main.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val GoogleIcon: ImageVector
    get() {
        if (_google != null) {
            return _google!!
        }
        _google = ImageVector.Builder(
            name = "Google", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
            viewportWidth = 18f,
            viewportHeight = 18f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF4285F4)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 4.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(17.64f, 9.2045f)
                curveToRelative(0.0f, -0.6364f, -0.0545f, -1.2545f, -0.1636f, -1.8545f)
                horizontalLineTo(9.0f)
                verticalLineToRelative(3.4818f)
                horizontalLineToRelative(4.8409f)
                curveToRelative(-0.2045f, 1.1273f, -0.8727f, 2.0727f, -1.7909f, 2.7182f)
                verticalLineToRelative(2.25f)
                horizontalLineToRelative(2.8818f)
                curveTo(16.7045f, 14.1818f, 17.64f, 11.9f, 17.64f, 9.2045f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF34A853)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 4.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(9.0f, 18.0f)
                curveToRelative(2.4318f, 0.0f, 4.4909f, -0.8045f, 5.9864f, -2.1818f)
                lineTo(12.0f, 13.5682f)
                curveToRelative(-0.8045f, 0.5455f, -1.8318f, 0.8636f, -3.0f, 0.8636f)
                curveToRelative(-2.3182f, 0.0f, -4.2818f, -1.5545f, -5.0f, -3.6545f)
                horizontalLineTo(1.0f)
                verticalLineToRelative(2.25f)
                curveTo(2.4545f, 15.6045f, 5.4273f, 18.0f, 9.0f, 18.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFBBC05)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 4.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(4.0f, 10.7727f)
                curveTo(3.8545f, 10.2273f, 3.75f, 9.6364f, 3.75f, 9.0f)
                curveToRelative(0.0f, -0.6364f, 0.1045f, -1.2273f, 0.25f, -1.7727f)
                verticalLineTo(4.95f)
                horizontalLineTo(1.0f)
                curveTo(0.375f, 6.2045f, 0.0f, 7.5545f, 0.0f, 9.0f)
                curveToRelative(0.0f, 1.4455f, 0.375f, 2.7955f, 1.0f, 4.05f)
                lineTo(4.0f, 10.7727f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEA4335)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 4.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(9.0f, 3.5682f)
                curveToRelative(1.3182f, 0.0f, 2.5f, 0.4545f, 3.4273f, 1.3182f)
                lineTo(15.0f, 2.3318f)
                curveTo(13.4909f, 0.8864f, 11.4318f, 0.0f, 9.0f, 0.0f)
                curveTo(5.4273f, 0.0f, 2.4545f, 2.3955f, 1.0f, 5.0f)
                lineTo(4.0f, 7.2273f)
                curveTo(4.7182f, 5.1227f, 6.6818f, 3.5682f, 9.0f, 3.5682f)
                close()
            }
        }.build()
        return _google!!
    }

private var _google: ImageVector? = null

val FacebookIcon: ImageVector
    get() {
        if (_facebook != null) {
            return _facebook!!
        }
        _facebook = ImageVector.Builder(
            name = "Facebook", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
            viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF1877F2)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 4.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(22.0f, 22.0f)
                verticalLineTo(2.0f)
                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                horizontalLineTo(4.0f)
                curveTo(2.9f, 0.0f, 2.0f, 0.9f, 2.0f, 2.0f)
                verticalLineToRelative(20.0f)
                curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(16.0f)
                curveTo(21.1f, 24.0f, 22.0f, 23.1f, 22.0f, 22.0f)
                close()
            }
            path(
                fill = SolidColor(Color.White),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 4.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(15.0f, 22.0f)
                verticalLineTo(13.5f)
                horizontalLineToRelative(2.5f)
                lineToRelative(0.4f, -3.0f)
                horizontalLineTo(15.0f)
                verticalLineTo(8.5f)
                curveToRelative(0.0f, -0.9f, 0.2f, -1.5f, 1.5f, -1.5f)
                horizontalLineTo(18.0f)
                verticalLineTo(4.3f)
                curveToRelative(-0.3f, -0.1f, -1.3f, -0.2f, -2.4f, -0.2f)
                curveTo(13.4f, 4.1f, 12.0f, 5.7f, 12.0f, 8.3f)
                verticalLineTo(10.5f)
                horizontalLineTo(9.5f)
                verticalLineToRelative(3.0f)
                horizontalLineTo(12.0f)
                verticalLineTo(22.0f)
                horizontalLineTo(15.0f)
                close()
            }
        }.build()
        return _facebook!!
    }

private var _facebook: ImageVector? = null
