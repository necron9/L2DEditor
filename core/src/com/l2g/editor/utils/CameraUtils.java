package com.l2g.editor.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.l2g.editor.L2DEditor;

/**
 * Управление камерой
 */
public abstract class CameraUtils {
    /**
     * DEFAULT_CAMERA_ZOOM_MIN - минимальный зум камеры по умолчанию
     */
    public static final float
            DEFAULT_CAMERA_ZOOM_MIN = 0.1f;
    /**
     * DEFAULT_CAMERA_SPEED - скорость перемещения камеры по умолчанию
     * DEFAULT_CAMERA_ZOOM_MIN - скорость зума камеры по умолчанию
     */
    public final static int
            DEFAULT_CAMERA_SPEED = 600,
            DEFAULT_CAMERA_ZOOM_SPEED = 8;

    public static final float DEFAULT_CAMERA_SMOOTH = 0.5f, DEFAULT_ZOOM_SMOOTH = 0.4f;
    public static short MOVE_STEP = 18;

    private static Camera cam;
    private static OrthographicCamera ortCam;

    private static Rectangle rect = new Rectangle(0, 0, L2DEditor.SCREEN_WIDTH , L2DEditor.SCREEN_HEIGHT);

    private static Vector3 targA = new Vector3(), targB = new Vector3();

    private static Vector2
            A = new Vector2(),
            B = new Vector2(),
            C = new Vector2(),
            D = new Vector2();

    private static Vector2
            start, direction, point;

    private static boolean isLocked, isCameraBorder,
            isOffset = true, isMoveStepX = true, isMoveStepY = true;

    private static int cameraSpeed, cameraZoomSpeed;

    private static float smot, cameraZoomMax, cameraZoomMin,
            cameraMinX, cameraMinY, cameraMaxX, cameraMaxY,
            defMinX, defMaxX, defMinY, defMaxY, offsetX, offsetY;

    /**
     * Состояния привязки камеры
     */
    private enum cameraSnapState {
        EMPTY, SNAP_TO_TARGET, SNAP_TO_TARGET_SMOOTH, MOVE_TO_TARGET_SMOOTH,
        SNAP_BETWEEN_TARGETS, SNAP_BETWEEN_TARGETS_SMOOTH;

        private static cameraSnapState snapState = EMPTY;
    }

    /**
     * Состояния масштабирования камеры
     */
    private enum cameraZoomState {
        EMPTY, ZOOM_IN_SMOOTH, ZOOM_OUT_SMOOTH;

        private static cameraZoomState zoomState = EMPTY;
    }

    /**
     * Состояния перемещения камеры
     */
    private enum cameraMoveState {
        EMPTY, MOVE_LEFT_SMOOTH, MOVE_RIGHT_SMOOTH,
        MOVE_UP_SMOOTH, MOVE_DOWN_SMOOTH;

        private static cameraMoveState moveHorizontalState = EMPTY;
        private static cameraMoveState moveVerticalState = EMPTY;
    }

    /**
     * Установить ограничение на перемещение камеры
     * @param border - ограничение перемещения
     */
    public static void setCameraBorder(boolean border) {
        isCameraBorder = border;
    }

    /**
     * Получить ограничение на перемещение камеры
     * @return - перемещение
     */
    public static boolean isCameraBorder() {
        return isCameraBorder;
    }

    /**
     * Определение вхождения ветора в границы камеры
     * @param vector3 - проверяемый вектор
     * @return - boolean
     */
    private static boolean checkVector(final Vector3 vector3) {
        if (vector3.x > cameraMinX && vector3.x < cameraMaxX &&
                vector3.y > cameraMinY && vector3.y < cameraMaxY)
            return true;
        return false;
    }

    /**
     * Определение вхождения ветора в границы камеры
     * @param vector2 - проверяемый вектор
     * @return - boolean
     */
    private static boolean checkVector(final Vector2 vector2) {
        if (vector2.x > cameraMinX && vector2.x < cameraMaxX &&
                vector2.y > cameraMinY && vector2.y < cameraMaxY)
            return true;
        return false;
    }

    /**
     * Определение вхождения оси Х ветора в границы камеры
     * @param vector3 - проверяемый вектор
     * @return - boolean
     */
    private static boolean checkVectorX(final Vector3 vector3) {
        if (vector3.x > cameraMinX && vector3.x < cameraMaxX)
            return true;
        return false;
    }

    /**
     * Определение вхождения оси Х ветора в границы камеры
     * @param vector2 - проверяемый вектор
     * @return - boolean
     */
    private static boolean checkVectorX(final Vector2 vector2) {
        if (vector2.x > cameraMinX && vector2.x < cameraMaxX)
            return true;
        return false;
    }

    /**
     * Определение вхождения значения в границы камеры
     * @param posX - проверяемое значение
     * @return - boolean
     */
    private static boolean checkVectorX(final float posX) {
        if (posX > cameraMinX && posX > cameraMaxX)
            return true;
        return false;
    }

    /**
     * Определение вхождения оси Y ветора в границы камеры
     * @param vector3 - проверяемый вектор
     * @return - boolean
     */
    private static boolean checkVectorY(final Vector3 vector3) {
        if (vector3.y > cameraMinY && vector3.y < cameraMaxY)
            return true;
        return false;
    }

    /**
     * Определение вхождения оси Y ветора в границы камеры
     * @param vector2 - проверяемый вектор
     * @return - boolean
     */
    private static boolean checkVectorY(final Vector2 vector2) {
        if (vector2.y > cameraMinY && vector2.y < cameraMaxY)
            return true;
        return false;
    }

    /**
     * Определение вхождения значения в границы камеры
     * @param posY - проверяемое значение
     * @return - boolean
     */
    private static boolean checkVectorY(final float posY) {
        if (posY > cameraMinY && posY < cameraMaxY)
            return true;
        return false;
    }

    /**
     * Установить позицию камеры
     * @param camera - камера
     * @param position - позиция
     */
    private static void setPosition(final Camera camera, final Vector3 position) {
        if (isCameraBorder) {
            if (checkVectorX(position))
                camera.position.x = position.x;
            if (checkVectorY(position))
                camera.position.y = position.y;
        } else
            camera.position.set(position);

        camera.update();
    }

    /**
     * Установить позицию камеры
     * @param camera - камера
     * @param position - позиция
     */
    private static void setPosition(final Camera camera, final Vector2 position) {
        if (isCameraBorder) {
            if (checkVectorX(position))
                camera.position.x = position.x;
            if (checkVectorY(position))
                camera.position.y = position.y;
        } else
            camera.position.set(position, 0);

        camera.update();
    }

    /**
     * Установить позицию камеры
     * @param camera - камера
     * @param positionX - позиция по Х
     * @param positionY - позиция по Y
     * @param positionZ - позиция по Z
     */
    private static void setPosition(final Camera camera, final float positionX, final float positionY, final float positionZ) {
        if (isCameraBorder) {
            if (checkVectorX(positionX))
                camera.position.x = positionX;
            if (checkVectorY(positionY))
                camera.position.y = positionY;
        } else
            camera.position.set(positionX, positionY, positionZ);

        camera.update();
    }

    /**
     * Установить позицию камеры по оси Х
     * @param camera - камера
     * @param positionX - позиция по Х
     */
    private static void setPositionX(Camera camera, float positionX) {
        if (isCameraBorder) {
            if (checkVectorX(positionX))
                camera.position.x = positionX;
        } else
            camera.position.x = positionX;

        camera.update();
    }

    /**
     * Установить позицию камеры по оси Y
     * @param camera - камера
     * @param positionY - позиция по Y
     */
    private static void setPositionY(final Camera camera, final float positionY) {
        if (isCameraBorder) {
            if (checkVectorY(positionY))
                camera.position.y = positionY;
        } else
            camera.position.y = positionY;

        camera.update();
    }

    /**
     * Установить зум камеры
     * @param camera - камера
     * @param zoom - зум
     */
    private static void setZoom(final OrthographicCamera camera, final float zoom) {
        if (zoom > cameraZoomMin && zoom < cameraZoomMax) {
            camera.zoom = zoom;
            camera.update();
        }
    }

    /**
     * Выравнивание камеры при достижении границы масштабирования
     * @param camera - камера
     */
    private static void alignZoom(final OrthographicCamera camera) {
        float scaleMin = ((L2DEditor.SCREEN_WIDTH - (L2DEditor.SCREEN_WIDTH  - camera.viewportWidth)) / camera.viewportWidth);
        float scaleMax = ((L2DEditor.SCREEN_WIDTH  + (L2DEditor.SCREEN_WIDTH  - camera.viewportWidth)) / camera.viewportWidth);

        float width = camera.viewportWidth * camera.zoom, height = camera.viewportHeight * camera.zoom;
        boolean isA, isB, isC, isD, isEmpty = false;

        cameraMaxX = defMaxX + (1 - camera.zoom) * defMaxX / scaleMax - 1;
        cameraMaxY = defMaxY + (1 - camera.zoom) * defMaxY / scaleMax - 1;
        cameraMinX = defMinX - (1 - camera.zoom) * defMinX / scaleMin + 1;
        cameraMinY = defMinY - (1 - camera.zoom) * defMinY / scaleMin + 1;

        if (isOffset) {
            offsetX = Math.abs(width - camera.viewportWidth) * .5f;
            offsetY = Math.abs(height - camera.viewportHeight) * .5f;
            isOffset = false;
        }

        A.set(camera.position.x - width * .5f, camera.position.y + height * .5f);
        B.set(camera.position.x + width * .5f, camera.position.y + height * .5f);
        C.set(camera.position.x - width * .5f, camera.position.y - height * .5f);
        D.set(camera.position.x + width * .5f, camera.position.y - height * .5f);

        isA = rect.contains(A); isB = rect.contains(B);
        isC = rect.contains(C); isD = rect.contains(D);

        if (!isA && !isC && !isD) {
            camera.position.set(camera.position.x + offsetX, camera.position.y + offsetY, 0);
            isEmpty = true;
        } else if (!isA && !isB && !isC) {
            camera.position.set(camera.position.x + offsetX, camera.position.y - offsetY, 0);
            isEmpty = true;
        } else if (!isA && !isB && !isD){
            camera.position.set(camera.position.x - offsetX, camera.position.y - offsetY, 0);
            isEmpty = true;
        } else if (!isD && !isB && !isC) {
            camera.position.set(camera.position.x - offsetX, camera.position.y + offsetY, 0);
            isEmpty = true;
        } else if (!isA && !isB && isC && isD) {
            camera.position.y -= offsetY;
            isEmpty = true;
        } else if (!isC && !isD && isA && isB) {
            camera.position.y += offsetY;
            isEmpty = true;
        } else if (!isA && !isC && isB && isD) {
            camera.position.x += offsetX;
            isEmpty = true;
        } else if (!isD && !isB && isC && isA) {
            camera.position.x -= offsetX;
            isEmpty = true;
        }

        if (isEmpty) {
            if (camera.position.x < cameraMinX)
                camera.position.x = cameraMinX;
            else if (camera.position.x > cameraMaxX)
                camera.position.x = cameraMaxX;

            if (camera.position.y < cameraMinY)
                camera.position.y = cameraMinY;
            else if (camera.position.y > cameraMaxY)
                camera.position.y = cameraMaxY;

            camera.update();

            cameraMoveState.moveHorizontalState = cameraMoveState.EMPTY;
            cameraMoveState.moveVerticalState = cameraMoveState.EMPTY;
            cameraSnapState.snapState = cameraSnapState.EMPTY;
            cameraZoomState.zoomState = cameraZoomState.EMPTY;
        }
    }

    /**
     * Установить начальную точку
     * @param x - позиция по Х
     * @param y - позиция по Y
     */
    public static void setStartPoint(final float x, final float y) {
        if (start == null) {
            start = new Vector2(x, y);
        } else
            start.set(x, y);

        if (point == null) {
            point = new Vector2(x, y);
        } else
            point.set(x, y);
    }

    /**
     * Определить направление движения по оси Х
     * @param direction - напрваление
     * @param screenX
     * @param lengthA
     * @param lengthB
     */
    private static void getDirectionX(final Vector2 direction, final float screenX, final double lengthA, final double lengthB) {
        if (screenX > (start.x + MOVE_STEP)) {
            isMoveStepX = true;
            direction.x = -(float) Math.sin(Math.atan(lengthA / lengthB));
        } else if (screenX < (start.x - MOVE_STEP)) {
            isMoveStepX = true;
            direction.x = (float) Math.sin(Math.atan(lengthA / lengthB));
        } else {
            isMoveStepX = false;
            direction.x = 0;
        }

        if (isMoveStepX) {
            if (point.x < screenX) direction.x = -(float) Math.sin(Math.atan(lengthA / lengthB));
            if (point.x > screenX) direction.x = (float) Math.sin(Math.atan(lengthA / lengthB));
        }
    }

    /**
     * Определить направление движения по оси Y
     * @param direction - направление
     * @param screenY
     * @param lengthA
     * @param lengthB
     */
    private static void getDirectionY(final Vector2 direction, final float screenY, final double lengthA, final double lengthB) {
        if (screenY > (start.y + MOVE_STEP)) {
            isMoveStepY = true;
            direction.y = (float) Math.cos(Math.atan(lengthA / lengthB));
        } else if (screenY < (start.y - MOVE_STEP)) {
            isMoveStepY = true;
            direction.y = -(float) Math.cos(Math.atan(lengthA / lengthB));
        } else {
            isMoveStepY = false;
            direction.y = 0;
        }
        if (isMoveStepY) {
            if (point.y < screenY) direction.y = (float) Math.cos(Math.atan(lengthA / lengthB));
            if (point.y > screenY) direction.y = - (float) Math.cos(Math.atan(lengthA / lengthB));
        }
    }

    /**
     * Переместить камеру с помощью перетаскивания
     * @param camera - камера
     * @param screenX - позиция по Х
     * @param screenY - позиция по Y
     */
    public static void moveToTouch(final Camera camera, final float screenX, final float screenY) {
        double lengthA = Math.sqrt(Math.pow(start.x - screenX, 2));
        double lengthB = Math.sqrt(Math.pow(start.y - screenY, 2));

        if (direction == null)
            direction = new Vector2();

        getDirectionX(direction, screenX, lengthA, lengthB);
        getDirectionY(direction, screenY, lengthA, lengthB);

        direction.nor().scl(getCameraSpeed() * Gdx.graphics.getDeltaTime());

        direction.add(camera.position.x, camera.position.y);
        CameraUtils.moveToTarget(camera, direction);

        point.set(screenX, screenY);
    }

    /**
     * Привязать камеру к точке
     * @param camera - камера
     * @param target - точка
     */
    public static void snapToTarget(final Camera camera, final Vector2 target) {
        cam = camera;
        targA.set(target, 0);

        cameraSnapState.snapState = cameraSnapState.SNAP_TO_TARGET;
        cameraMoveState.moveHorizontalState = cameraMoveState.EMPTY;
        cameraMoveState.moveVerticalState = cameraMoveState.EMPTY;

        isLocked = true;
    }

    /**
     * Плавно привязать камеру к точке
     * @param camera - камера
     * @param target - точка
     * @param smooth - сглаживание
     */
    public static void snapToTarget(final Camera camera, final Vector2 target, final float smooth) {
        cam = camera;
        targA.set(target, 0);
        smot = smooth;

        cameraSnapState.snapState = cameraSnapState.SNAP_TO_TARGET;
        cameraMoveState.moveHorizontalState = cameraMoveState.EMPTY;
        cameraMoveState.moveVerticalState = cameraMoveState.EMPTY;

        isLocked = true;
    }

    /**
     * Привязать камеру к точкам
     * @param camera - камера
     * @param targetA - точка A
     * @param targetB - точка B
     */
    public static void snapBetweenTargets(final Camera camera, final Vector2 targetA, final Vector2 targetB) {
        cam = camera;
        targA.set(targetA, 0);
        targB.set(targetB, 0);

        cameraSnapState.snapState = cameraSnapState.SNAP_TO_TARGET;
        cameraMoveState.moveHorizontalState = cameraMoveState.EMPTY;
        cameraMoveState.moveVerticalState = cameraMoveState.EMPTY;

        isLocked = true;
    }

    /**
     * Плавно привязать камеру к точкам
     * @param camera - камера
     * @param targetA - точка A
     * @param targetB - точка B
     * @param smooth - сглаживание
     */
    public static void snapBetweenTargets(final Camera camera, final Vector2 targetA, final Vector2 targetB, final float smooth) {
        cam = camera;
        targA.set(targetA, 0);
        targB.set(targetB, 0);
        smot = smooth;

        cameraSnapState.snapState = cameraSnapState.SNAP_TO_TARGET;
        cameraMoveState.moveHorizontalState = cameraMoveState.EMPTY;
        cameraMoveState.moveVerticalState = cameraMoveState.EMPTY;

        isLocked = true;
    }

    /**
     * Переместиь камеру к точке
     * @param camera - камера
     * @param target - точка
     */
    public static void moveToTarget(final Camera camera, final Vector2 target) {
        setPosition(camera, target);

        cameraSnapState.snapState = cameraSnapState.EMPTY;
        cameraMoveState.moveHorizontalState = cameraMoveState.EMPTY;
        cameraMoveState.moveVerticalState = cameraMoveState.EMPTY;

        isLocked = false;
    }

    /**
     * Плавно переместить камеру к точке
     * @param camera - камера
     * @param target - точка
     * @param smooth - сглаживание
     */
    public static void moveToTarget(final Camera camera, final Vector2 target, final float smooth) {
        cam = camera;
        targA.set(target, targA.z);
        smot = smooth;

        cameraSnapState.snapState = cameraSnapState.MOVE_TO_TARGET_SMOOTH;
        cameraMoveState.moveHorizontalState = cameraMoveState.EMPTY;
        cameraMoveState.moveVerticalState = cameraMoveState.EMPTY;

        isLocked = false;
    }

    /**
     * Переместить камеру влево с заданной скоростью
     * @param camera - камера
     * @param speed - скорость
     */
    public static void moveToLeft(final Camera camera, final int speed) {
        Gdx.app.log("ffffff","");
        float offset = speed * Gdx.graphics.getDeltaTime();
        if (!isLocked) {
            if (isCameraBorder) {
                if ((camera.position.x - offset) > cameraMinX) {
                    setCameraSpeed(speed);
                    setPositionX(camera, camera.position.x - offset);
                }
            } else {
                setCameraSpeed(speed);
                setPositionX(camera, camera.position.x - offset);
            }
        }

        cameraMoveState.moveHorizontalState = cameraMoveState.EMPTY;
        cameraSnapState.snapState = cameraSnapState.EMPTY;
    }

    /**
     * Переместить камеру вправо с заданной скоростью
     * @param camera - камера
     * @param speed - скорость
     */
    public static void moveToRight(final Camera camera, final int speed) {
        float offset = speed * Gdx.graphics.getDeltaTime();

        if (!isLocked) {
            if (isCameraBorder) {
                if ((camera.position.x + offset) < cameraMaxX) {
                    setCameraSpeed(speed);
                    setPositionX(camera, camera.position.x + offset);
                }
            } else {
                setCameraSpeed(speed);
                setPositionX(camera, camera.position.x + offset);
            }
        }

        cameraMoveState.moveHorizontalState = cameraMoveState.EMPTY;
        cameraSnapState.snapState = cameraSnapState.EMPTY;
    }

    /**
     * Переместить камеру вниз с заданной скоростью
     * @param camera - камера
     * @param speed - скорость
     */
    public static void moveToDown(final Camera camera, final int speed) {
        float offset = speed * Gdx.graphics.getDeltaTime();

        if (!isLocked) {
            if (isCameraBorder) {
                if ((camera.position.y - offset) > cameraMinY) {
                    setCameraSpeed(speed);
                    setPositionY(camera, camera.position.y - offset);
                }
            } else {
                setCameraSpeed(speed);
                setPositionY(camera, camera.position.y - offset);
            }
        }

        cameraMoveState.moveVerticalState = cameraMoveState.EMPTY;
        cameraSnapState.snapState = cameraSnapState.EMPTY;
    }

    /**
     * Переместить камеру вверх с заданной скоростью
     * @param camera - камера
     * @param speed - скорость
     */
    public static void moveToUp(final Camera camera, final int speed) {
        float offset = speed * Gdx.graphics.getDeltaTime();

        if (!isLocked) {
            if (isCameraBorder) {
                if ((camera.position.y + offset) < cameraMaxY) {
                    setCameraSpeed(speed);
                    setPositionY(camera, camera.position.y + offset);
                }
            } else {
                setCameraSpeed(speed);
                setPositionY(camera, camera.position.y + offset);
            }
        }

        cameraMoveState.moveVerticalState = cameraMoveState.EMPTY;
        cameraSnapState.snapState = cameraSnapState.EMPTY;
    }

    /**
     * Переместить камеру влево с установленной скоростью
     * @param camera - камера
     */
    public static void moveToLeft(final Camera camera) {
        moveToLeft(camera, cameraSpeed);
    }

    /**
     * Переместить камеру вправо с установленной скоростью
     * @param camera - камера
     */
    public static void moveToRight(final Camera camera) {
        moveToRight(camera, cameraSpeed);
    }

    /**
     * Переместить камеру вниз с установленной скоростью
     * @param camera - камера
     */
    public static void moveToDown(final Camera camera) {
        moveToDown(camera, cameraSpeed);
    }

    /**
     * Переместить камеру вверх с установленной скоростью
     * @param camera - камера
     */
    public static void moveToUp(final Camera camera) {
        moveToUp(camera, cameraSpeed);
    }

    /**
     * Переместить камеру влево с установленной скоростью и заданным сглаживанием
     * @param camera - камера
     */
    public static void moveToLeft(final Camera camera, final float smooth) {
        moveToLeft(camera, cameraSpeed, smooth);
    }

    /**
     * Переместить камеру вправо с установленной скоростью и заданным сглаживанием
     * @param camera - камера
     */
    public static void moveToRight(final Camera camera, final float smooth) {
        moveToRight(camera, cameraSpeed, smooth);
    }

    /**
     * Переместить камеру вверх с установленной скоростью и заданным сглаживанием
     * @param camera - камера
     */
    public static void moveToUp(final Camera camera, final float smooth) {
        moveToUp(camera, cameraSpeed, smooth);
    }

    /**
     * Переместить камеру вниз с установленной скоростью и заданным сглаживанием
     * @param camera - камера
     */
    public static void moveToDown(final Camera camera, final float smooth) {
        moveToDown(camera, cameraSpeed, smooth);
    }

    /**
     * Переместить камеру влево с заданной скоростью и сглаживанием
     * @param camera - камера
     */
    public static void moveToLeft(final Camera camera, final int speed, final float smooth) {
        float offset = speed * Gdx.graphics.getDeltaTime();

        cam = camera;
        if (isCameraBorder) {
            if ((targA.x - offset) > cameraMinX)
                targA.x = camera.position.x - offset;
            else targA.x = camera.position.x;
        } else
            targA.x = camera.position.x - offset;


        smot = smooth;
        setCameraSpeed(speed);

        cameraMoveState.moveHorizontalState = cameraMoveState.MOVE_LEFT_SMOOTH;
        cameraSnapState.snapState = cameraSnapState.EMPTY;
    }

    /**
     * Переместить камеру вправо с заданной скоростью и сглаживанием
     * @param camera - камера
     */
    public static void moveToRight(final Camera camera, final int speed, final float smooth) {
        float offset = speed * Gdx.graphics.getDeltaTime();

        cam = camera;
        if (isCameraBorder) {
            if ((targA.x + offset) < cameraMaxX)
                targA.x = camera.position.x + offset;
            else targA.x = camera.position.x;
        } else
            targA.x = camera.position.x + offset;

        smot = smooth;
        setCameraSpeed(speed);

        cameraMoveState.moveHorizontalState = cameraMoveState.MOVE_RIGHT_SMOOTH;
        cameraSnapState.snapState = cameraSnapState.EMPTY;
    }

    /**
     * Переместить камеру вверх с заданной скоростью и сглаживанием
     * @param camera - камера
     */
    public static void moveToUp(final Camera camera, final int speed, final float smooth) {
        float offset = speed * Gdx.graphics.getDeltaTime();

        cam = camera;
        if (isCameraBorder) {
            if ((targA.y + offset) < cameraMaxY)
                targA.y = camera.position.y + offset;
            else targA.y = camera.position.y;
        } else
            targA.y = camera.position.y + offset;

        smot = smooth;
        setCameraSpeed(speed);

        cameraMoveState.moveVerticalState = cameraMoveState.MOVE_UP_SMOOTH;
        cameraSnapState.snapState = cameraSnapState.EMPTY;
    }

    /**
     * Переместить камеру вниз с заданной скоростью и сглаживанием
     * @param camera - камера
     */
    public static void moveToDown(final Camera camera, final int speed, final float smooth) {
        float offset = speed * Gdx.graphics.getDeltaTime();

        cam = camera;
        if (isCameraBorder) {
            if ((targA.y - offset) > cameraMinY)
                targA.y = camera.position.y - offset;
            else targA.y = camera.position.y;
        } else
            targA.y = camera.position.y - offset;

        smot = smooth;
        setCameraSpeed(speed);

        cameraMoveState.moveVerticalState = cameraMoveState.MOVE_DOWN_SMOOTH;
        cameraSnapState.snapState = cameraSnapState.EMPTY;
    }


    /**
     * Увеличить масштаб камеры
     * @param camera - камера
     */
    public static void zoomIn(final OrthographicCamera camera) {
        zoomIn(camera, DEFAULT_ZOOM_SMOOTH);
    }

    /**
     * Уменьшить масштаб камеры
     * @param camera - камера
     */
    public static void zoomOut(final OrthographicCamera camera) {
        zoomOut(camera, DEFAULT_ZOOM_SMOOTH);
    }

    /**
     * Плавно увеличить масштаб камеры
     * @param camera - камера
     * @param smooth - сглаживание
     */
    public static void zoomIn(final OrthographicCamera camera, final float smooth) {
        zoomIn(camera, cameraZoomSpeed, smooth);
    }

    /**
     * Плавно уменьшить масштаб камеры
     * @param camera - камера
     * @param smooth - сглаживание
     */
    public static void zoomOut(final OrthographicCamera camera, final float smooth) {
        zoomOut(camera, cameraZoomSpeed, smooth);
    }

    /**
     * Плавно увеличить масштаб камеры с заданной скоростью
     * @param camera - камера
     * @param speed - скорость
     * @param smooth - сглаживание
     */
    public static void zoomIn(final OrthographicCamera camera, final int speed, final float smooth) {
        float offset = speed * Gdx.graphics.getDeltaTime();

        cam = camera;
        ortCam = camera;
        smot = smooth;

        if (isCameraBorder) {
            if ((camera.zoom - offset) > cameraZoomMin) {
                targA.z = camera.zoom - offset;
                cameraZoomState.zoomState = cameraZoomState.ZOOM_IN_SMOOTH;
            }
        } else {
            targA.z = camera.zoom - offset;
            cameraZoomState.zoomState = cameraZoomState.ZOOM_IN_SMOOTH;
        }
    }

    /**
     * Плавно уменьшить масштаб камеры с заданной скоростью
     * @param camera - камера
     * @param speed - скорость
     * @param smooth - сглаживание
     */
    public static void zoomOut(final OrthographicCamera camera, final int speed, final float smooth) {
        float offset = speed * Gdx.graphics.getDeltaTime();

        cam = camera;
        ortCam = camera;
        smot = smooth;

        if (isCameraBorder) {
            if ((camera.zoom + offset) < cameraZoomMax) {
                targA.z = camera.zoom + offset;
                cameraZoomState.zoomState = cameraZoomState.ZOOM_OUT_SMOOTH;
            }
        } else {
            targA.z = camera.zoom + offset;
            cameraZoomState.zoomState = cameraZoomState.ZOOM_OUT_SMOOTH;
        }
    }

    /**
     * Установить скорость перемещения камеры
     * @param speed - скорость перемещения камеры
     */
    public static void setCameraSpeed(final int speed) {
        cameraSpeed = speed;
    }

    /**
     * Получить скорость перемещения камеры
     * @return - int
     */
    public static int getCameraSpeed() {
        return cameraSpeed;
    }

    /**
     * Установить скорость зума камеры
     * @param speed
     */
    public static void setCameraZoomSpeed(final int speed) {
        cameraZoomSpeed = speed;
    }

    /**
     * Получить скорость зума камеры
     * @return int
     */
    public static int getCameraZoomSpeed() {
        return cameraZoomSpeed;
    }

    /**
     * Установить минимальный зум камеры
     * @param zoomMin - минимальный зум
     */
    public static void setCameraZoomMin(final float zoomMin) {
        cameraZoomMin = zoomMin;
    }

    /**
     * Получить минимальный зум камеры
     * @return - float
     */
    public static float getCameraZoomMin() {
        return cameraZoomMin;
    }

    /**
     * Установить максимальный зум камеры
     * @param zoomMax - максимальный зум камеры
     */
    public static void setCameraZoomMax(final float zoomMax) {
        cameraZoomMax = zoomMax;
    }

    /**
     * Получить максимальный зум камеры
     * @return - float
     */
    public static float getCameraZoomMax() {
        return cameraZoomMax;
    }

    /**
     * Определить максимальный зум камеры исходя из размеров карты
     * @param camera - камера
     * @param map - карта
     */
    public static void setCameraZoomMax(final OrthographicCamera camera, final TiledMap map) {
        float zoomWidth = L2DEditor.SCREEN_WIDTH / camera.viewportWidth;
        float zoomHeight = L2DEditor.SCREEN_HEIGHT / camera.viewportHeight;

        if (zoomWidth < zoomHeight) cameraZoomMax = zoomWidth;
        else cameraZoomMax = zoomHeight;
    }

    /**
     * Установить минимальную позицию камеры по осям Х и Y
     * @param minX - позиция по Х
     * @param minY - позиция по Y
     */
    public static void setCameraMin(final float minX, final float minY) {
        cameraMinX = minX;
        cameraMinY = minY;

        defMinX = minX;
        defMinY = minY;
    }

    /**
     * Установить минимальную позицию камеры по осям Х и Y исходя из окна проекции
     * @param viewport - окно проекции
     */
    public static void setCameraMin(final Viewport viewport) {
        CameraUtils.setCameraMin(viewport.getWorldWidth() * .5f, viewport.getWorldHeight() * .5f);
    }

    /**
     * Установить максимальную позицию камеры по осям Х и Y
     * @param maxX - позиция по Х
     * @param maxY - позиция по Y
     */
    public static void setCameraMax(final float maxX, final float maxY) {
        cameraMaxX = maxX;
        cameraMaxY = maxY;

        defMaxX = maxX;
        defMaxY = maxY;
    }

    /**
     * Установить максимальную позицию камеры по осям Х и Y исходя из карты
     * @param map - карта
     */
    public static void setCameraMax(final TiledMap map) {
        setCameraMax(L2DEditor.SCREEN_WIDTH - CameraUtils.getCameraMinX(), L2DEditor.SCREEN_HEIGHT - CameraUtils.getCameraMinY());
    }

    /**
     * Получить минимальную позицию камеры по оси Х
     * @return - float
     */
    public static float getCameraMinX() {
        return cameraMinX;
    }

    /**
     * Получить минимальную позицию камеры по оси Y
     * @return - float
     */
    public static float getCameraMinY() {
        return cameraMinY;
    }

    /**
     * Получить макмимальную позицию камеры по оси Х
     * @return
     */
    public static float getCameraMaxX() {
        return cameraMaxX;
    }

    /**
     * Получить макмимальную позицию камеры по оси Y
     * @return
     */
    public static float getCameraMaxY() {
        return cameraMaxY;
    }

    /**
     * Обновление параметров камеры
     */
    public static void update() {
        if (cam != null && ortCam != null) {
            switch (cameraSnapState.snapState) {
                case SNAP_TO_TARGET:
                    setPosition(cam, targA);
                    break;
                case SNAP_TO_TARGET_SMOOTH:
                    setPosition(cam, cam.position.x + (targA.x - cam.position.x) * smot, cam.position.y + (targA.y - cam.position.y) * smot, 0);
                    break;
                case SNAP_BETWEEN_TARGETS_SMOOTH:
                    setPosition(cam, cam.position.x + ((targA.x + targB.x) * .5f - cam.position.x) * smot,
                            cam.position.y + ((targA.y + targB.y) * .5f - cam.position.y) * smot, 0);
                    break;
                case MOVE_TO_TARGET_SMOOTH:
                    setPosition(cam, cam.position.x + (targA.x - cam.position.x) * smot,
                            cam.position.y + (targA.y - cam.position.y) * smot, 0);
                    break;
            }

            switch (cameraMoveState.moveVerticalState) {
                case MOVE_DOWN_SMOOTH:
                    if (!isLocked) {
                        if (isCameraBorder) {
                            if (cam.position.y > cameraMinY)
                                setPositionY(cam, targA.y);
                        } else
                            setPositionY(cam, targA.y);
                    }
                    break;
                case MOVE_UP_SMOOTH:
                    Gdx.app.log("UP","");
                    if (!isLocked) {
                        if (isCameraBorder) {
                            if (cam.position.y < cameraMaxY)
                                setPositionY(cam, targA.y);
                        } else
                            setPositionY(cam, targA.y);
                    }
                    break;
            }

            switch (cameraMoveState.moveHorizontalState) {
                case MOVE_LEFT_SMOOTH:
                    Gdx.app.log("LE","");
                    if (!isLocked) {
                        if (isCameraBorder) {
                            if (cam.position.x > cameraMinX)
                                setPositionX(cam, targA.x);
                        } else
                            setPositionX(cam, targA.x);
                    }
                    break;
                case MOVE_RIGHT_SMOOTH:
                    if (!isLocked) {
                        if (isCameraBorder) {
                            if (cam.position.x < cameraMaxX)
                                setPositionX(cam, targA.x);
                        } else
                            setPositionX(cam, targA.x);
                    }
                    break;
            }

            switch (cameraZoomState.zoomState) {
                case ZOOM_IN_SMOOTH:
                    setZoom(ortCam, ortCam.zoom + (targA.z - ortCam.zoom) * smot);

                    if (isCameraBorder) alignZoom(ortCam);
                    break;
                case ZOOM_OUT_SMOOTH:
                    setZoom(ortCam, ortCam.zoom + (targA.z - ortCam.zoom) * smot);

                    if (isCameraBorder) alignZoom(ortCam);
                    break;
            }
        }
    }
}