import numpy as np
from PIL import Image, ImageOps, ImageDraw
from tflite_runtime.interpreter import Interpreter

def getClothePoints(image_uri, model_path):

    interpreter = Interpreter(model_path=model_path)
    interpreter.allocate_tensors()

    img = Image.open(image_uri).convert("RGB")
    img = ImageOps.exif_transpose(img)
    original_w, original_h = img.size
    resized_img = img.resize((640, 640))
    input_data = np.expand_dims(np.array(resized_img) / 255.0, axis=0).astype(np.float32)

    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()

    interpreter.set_tensor(input_details[0]['index'], input_data)

    interpreter.invoke()

    output = interpreter.get_tensor(output_details[0]['index'])
    best_score = 0
    left_shoulder=[]
    right_shoulder=[]
    left_hips=[]
    right_hips=[]

    draw = ImageDraw.Draw(img)

    for i in range(output.shape[2]):
        score = output[0, 4, i]  # Confidence score at index 4
        if score > best_score:
            best_score = score
            left_shoulder = output[0, 6:8, i]
            left_hips = output[0, 9:11, i]
            right_hips = output[0, 12:14, i]
            right_shoulder = output[0, 15:17, i]


    scale_x = original_w / 640
    scale_y = original_h / 640

    left_shoulder = [left_shoulder[0] * scale_x, left_shoulder[1] * scale_y]
    right_shoulder = [right_shoulder[0] * scale_x, right_shoulder[1] * scale_y]
    left_hips = [left_hips[0] * scale_x, left_hips[1] * scale_y]
    right_hips = [right_hips[0] * scale_x, right_hips[1] * scale_y]


    return [left_shoulder, right_shoulder, left_hips, right_hips]