import numpy as np
from PIL import Image, ImageOps
from tflite_runtime.interpreter import Interpreter

def cut_clothe_from_image(image_uri, model_path):
    # Initialize the TensorFlow Lite interpreter
    interpreter = Interpreter(model_path=model_path)
    interpreter.allocate_tensors()

    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()

    # Load the image from the URI
    img = Image.open(image_uri).convert("RGB")
    img = ImageOps.exif_transpose(img)

    original_w, original_h = img.size

    # Preprocess the image
    resized_img = img.resize((640, 640))
    input_data = np.expand_dims(np.array(resized_img) / 255.0, axis=0).astype(np.float32)

    # Set input tensor and run inference
    interpreter.set_tensor(input_details[0]['index'], input_data)
    interpreter.invoke()

    # Extract detection and mask results
    detections = interpreter.get_tensor(output_details[0]['index'])
    masks = interpreter.get_tensor(output_details[1]['index'])

    prototypes = masks[0]  # Prototype masks
    num_detections = detections.shape[2]

    best_score = 0
    best_extracted_object = None

    for i in range(num_detections):
        score = detections[0, 4, i]
        if score > best_score:
            # Decode bounding box
            x_center, y_center, width, height = detections[0, :4, i]
            x_min = round((x_center - width / 2) * original_w)
            y_min = round((y_center - height / 2) * original_h)
            x_max = round((x_center + width / 2) * original_w)
            y_max = round((y_center + height / 2) * original_h)

            # Ensure bounding box coordinates are within image dimensions
            x_min, x_max = max(0, x_min), min(original_w, x_max)
            y_min, y_max = max(0, y_min), min(original_h, y_max)

            # Generate mask
            mask_coefficients = detections[0, 5:37, i]
            mask = np.dot(prototypes, mask_coefficients)
            mask = 1 / (1 + np.exp(-mask))  # Apply sigmoid

            # Resize and crop mask
            mask_resized = np.array(Image.fromarray(mask).resize((original_w, original_h)))
            mask_binary = (mask_resized > 0.5).astype(np.uint8)

            mask_cropped = np.zeros_like(mask_binary)
            mask_cropped[y_min:y_max, x_min:x_max] = mask_binary[y_min:y_max, x_min:x_max]

            # Combine mask with the original image to create RGBA output
            img_np = np.array(img)
            rgba_image = np.zeros((original_h, original_w, 4), dtype=np.uint8)
            rgba_image[..., :3] = img_np
            rgba_image[..., 3] = mask_cropped * 255

            # Update best score and object
            best_extracted_object = Image.fromarray(rgba_image, "RGBA")
            best_score = score
    best_extracted_object.save(image_uri,format="PNG")

    return "mam dosc juz tego"