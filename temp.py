# From Python
# It requires OpenCV installed for Python
import sys
import cv2
import os
from sys import platform
from math import acos, atan, degrees, sqrt
import argparse
import time
import imutils
import pymysql
from datetime import datetime


def getAngle(A, B, C):

    if (0 not in [A[2], B[2], C[2]]):

        AB = sqrt((A[0] - B[0]) ** 2 + (A[1] - B[1]) ** 2)
        AC = sqrt((A[0] - C[0]) ** 2 + (A[1] - C[1]) ** 2)
        BC = sqrt((C[0] - B[0]) ** 2 + (C[1] - B[1]) ** 2)

        angle = degrees(acos((AB ** 2 + BC ** 2 - AC ** 2) / (2 * AB * BC)))
        return angle
    else:
        print("Not all points found! Cannot calculate Angle!")
        return 0


def getAngle2(A, B):
    if (0 not in [A[2], B[2]]):
        x1 = A[0]
        y1 = A[1]
        x2 = B[0]
        y2 = B[1]
        angle = (180 + degrees(atan((y1 - y2) / (x1 - x2)))) % 180
        return angle
    else:
        return 0

conn = pymysql.connect(host='localhost', user='root', password='',
                       db='kbs', charset='utf8')
curs = conn.cursor()
datenow = datetime.today().strftime("%Y-%m-%d")  # YYYY/mm/dd HH:MM:SS 형태의 시간 출력

def getInstruction(angle, hand_pos, Instruction):
    # Returns (hand_pos, +=reps, Instruction)
    if angle != 0:
        if hand_pos == 0 and angle < 40:
            return (1, 0, " DOWN!            ")
        elif hand_pos == 1 and angle > 160:
            return (0, 1, "UP!             ")
    return (hand_pos, 0, Instruction)

g_reps=0

# Import Openpose (Windows/Ubuntu/OSX)
dir_path = os.path.dirname(os.path.realpath(__file__))
try:
    # Windows Import
    if platform == "win32":
        # Change these variables to point to the correct folder (Release/x64 etc.)
        sys.path.append(dir_path + '/../../python/openpose/Release');
        os.environ['PATH'] = os.environ['PATH'] + ';' + dir_path + '/../../x64/Release;' + dir_path + '/../../bin;'
        import pyopenpose as op
    else:
        # Change these variables to point to the correct folder (Release/x64 etc.)
        sys.path.append('../../python');
        # If you run `make install` (default path is `/usr/local/python` for Ubuntu), you can also access the OpenPose/python module from there. This will install Open
        # sys.path.append('/usr/local/python')
        from openpose import pyopenpose as op
except ImportError as e:
    print(
        'Error: OpenPose library could not be found. Did you enable `BUILD_PYTHON` in CMake and have this Python script in the right folder?')
    raise e

# Flags
parser = argparse.ArgumentParser()
# parser.add_argument("--image_dir", default="../../../examples/media/", help="Process a directory of images. Read all standard formats (jpg, png, bmp, etc.).")
parser.add_argument("--no_display", default=False, help="Enable to disable the visual display.")
args = parser.parse_known_args()

# Custom Params (refer to include/openpose/flags.hpp for more parameters)
params = dict()
params["model_folder"] = "../../../models/"

params["net_resolution"] = "-1x320"

for i in range(0, len(args[1])):
    curr_item = args[1][i]
    if i != len(args[1]) - 1:
        next_item = args[1][i + 1]
    else:
        next_item = "1"
    if "--" in curr_item and "--" in next_item:
        key = curr_item.replace('-', '')
        if key not in params:  params[key] = "1"
    elif "--" in curr_item and "--" not in next_item:
        key = curr_item.replace('-', '')
        if key not in params: params[key] = next_item


try:

    # Starting OpenPose
    opWrapper = op.WrapperPython()
    opWrapper.configure(params)
    opWrapper.start()

#'http://192.168.219.140:8080/video'
    stream = cv2.VideoCapture('http://192.168.219.156:8000/video')

    font = cv2.FONT_HERSHEY_SIMPLEX
    reps = 0
    sets = 0
    hand_pos = 0  # 0 -> greater than 160; 1 -> less than 40
    right_hand = True  # True -> Right        ; False -> Left 
    Instruction = ""
    Instruction_c1 = "    Start Now!   "
    Instruction_c2 = "    Do 3 Bicep Curls        "

    min_max = 0
    badpos = 0

    while True:

        ret, img = stream.read()

        datum = op.Datum()
        datum.cvInputData = img
        opWrapper.emplaceAndPop([datum])

        # Display the stream
        # if not args[0].no_display:
        op_img = datum.cvOutputData
        op_img = cv2.flip(op_img, 1);

        op_img = cv2.resize(op_img, (1920,1080), fx=0, fy=0, interpolation = cv2.INTER_LINEAR)

        try:
            r_shoulder = datum.poseKeypoints[0][2]
            r_elbow = datum.poseKeypoints[0][3]
            r_wrist = datum.poseKeypoints[0][4]

            l_shoulder = datum.poseKeypoints[0][5]
            l_elbow = datum.poseKeypoints[0][6]
            l_wrist = datum.poseKeypoints[0][7]

            b_top = datum.poseKeypoints[0][1]
            b_bottom = datum.poseKeypoints[0][8]

            r_angle = getAngle(r_shoulder, r_elbow, r_wrist)  # Angle at right elbow
            l_angle = getAngle(l_shoulder, l_elbow, l_wrist)  # Angle at left elbow
            b_angle = getAngle2(b_top, b_bottom)  # Angle between your Back and the ground
            ra_angle = getAngle2(r_shoulder, r_elbow)  # Angle between right upper arm (shoulder-elbow) and ground
            la_angle = getAngle2(l_shoulder, l_elbow)  # Angle between left upper arm (shoulder-elbow) and ground

           
            print("\nBody keypoints: \n" + "rw: " + str(r_wrist) + "\nre: " + str(r_elbow) + "\nrs: " + str(r_shoulder))
            print("Right hand Angle: ", r_angle)
            print("Left hand Angle: ", l_angle)
            print("Back to Right Hand Angle: ", abs(b_angle - ra_angle))

            if reps == 0 and hand_pos == 1:
                Instruction_c2 = ""

            if right_hand:
                (hand_pos, r, Instruction_c1) = getInstruction(r_angle, hand_pos, Instruction_c1)
                reps += r
                angle = r_angle
                Instruction = "Turn : Right"
                if not reps == 0 or not hand_pos == 0:
                    if 0 not in [b_angle, ra_angle] and abs(b_angle - ra_angle) >= 10:
                        if bad_pos > 5:  # Waiting a few iterations to avoid on and off flickering of this message
                            Instruction_c2 = ""
                        else:
                            bad_pos += 1
                    else:
                        Instruction_c2 = ""
                        bad_pos = 0
            else:
                (hand_pos, r, Instruction_c1) = getInstruction(l_angle, hand_pos, Instruction_c1)
                reps += r
                angle = l_angle
                Instruction = "Turn : Left"
                if not reps == 0 or not hand_pos == 0:
                    if 0 not in [b_angle, la_angle] and abs(b_angle - la_angle) >= 10:
                        if bad_pos > 5:  # Waiting a few iterations to avoid on and off flickering of this message
                            Instruction_c2 = ""
                        else:
                            bad_pos += 1
                    else:
                        Instruction_c2 = ""
                        bad_pos = 0

            if abs(90 - b_angle) >= 10:
                Instruction_c2 = "    Stand Straight.    "

            if reps == 3 and right_hand:
                reps = 0
                Instruction_c1 = ""
                Instruction_c2 = "Now do 3 reps on the Left."
                right_hand = False
            elif reps == 3 and not right_hand:
                reps = 0
                sets += 1
                sql = "update record set reps=reps+1 where name=%s and date=%s"
                val = ('me', datenow)
                curs.execute(sql, val)

                conn.commit()

                Instruction_c1 = "Completed " + str(sets) + " Sets      "
                Instruction_c2 = "Now do 3 reps on the Right."
                right_hand = True

            cv2.putText(op_img, Instruction, (1500, 630), font, 1.5, (0, 0, 255), 2, cv2.LINE_AA)
            cv2.putText(op_img, Instruction_c1, (980, 200), font, 1.2, (0, 0, 0), 3, cv2.LINE_AA)
            cv2.putText(op_img, Instruction_c2, (980, 430), font, 1.5, (0, 0, 255), 4, cv2.LINE_AA)
            cv2.putText(op_img, "Reps: " + str(reps), (1600, 250), font, 1.2, (0, 0, 0), 3, cv2.LINE_AA)
            cv2.putText(op_img, "Sets:  " + str(sets), (1600, 200), font, 1.2, (0, 0, 0), 3, cv2.LINE_AA)


        except Exception as e:
            tempm=e;
            #cv2.putText(op_img, 'No Person Found!', (980, 430), font, 1.25, (0, 0, 255), 3, cv2.LINE_AA)

        big_img = cv2.resize(op_img, (900, 1550), fx=0, fy=0, interpolation=cv2.INTER_CUBIC)

        # out.write(op_img)
        # scale=20
        img90 = cv2.rotate(big_img, cv2.ROTATE_90_CLOCKWISE)

        # get the webcam size
        # get the webcam size
        height, width, channels = img90.shape

        # prepare the crop
        centerX, centerY = 150, 200
        radiusX, radiusY = 750, 1150

        minX, maxX = centerX - radiusX, centerX + radiusX
        minY, maxY = centerY - radiusY, centerY + radiusY

        cropped = img90[minX:maxX, minY:maxY]

        resized_cropped = cv2.resize(cropped, (width, height))



        cv2.imshow("Bicep Curls", resized_cropped)
        if cv2.waitKey(1) == 27:
            break
        # esc to quit




    stream.release()
    cv2.destroyAllWindows()
    conn.close()
    opWrapper.stop()

except Exception as e:
    print(e)
    sys.exit(-1)


