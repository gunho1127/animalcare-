import sys
import numpy as np
import os
os.environ['TF_ENABLE_ONEDNN_OPTS'] = '0'
from tensorflow.keras.preprocessing.image import img_to_array, load_img
from tensorflow.keras.applications import InceptionV3, Xception, NASNetLarge, InceptionResNetV2
from tensorflow.keras.applications.inception_v3 import preprocess_input as inception_preprocessor
from tensorflow.keras.applications.xception import preprocess_input as xception_preprocessor
from tensorflow.keras.applications.nasnet import preprocess_input as nasnet_preprocessor
from tensorflow.keras.applications.inception_resnet_v2 import preprocess_input as inc_resnet_preprocessor
from tensorflow.keras.models import load_model
from tensorflow.keras.layers import Input, Lambda, GlobalAveragePooling2D
from tensorflow.keras.models import Model
import pandas as pd


# Load labels and model
labels = pd.read_csv('C:/Users/skm99/OneDrive/Desktop/수업자료/spring boot/testSpring/src/main/java/com/board/labels.csv')
classes = sorted(list(set(labels['breed'])))
model = load_model('C:/Users/skm99/OneDrive/Desktop/수업자료/spring boot/testSpring/src/main/java/com/board/my_model.h5')


def get_features(model_name, model_preprocessor, input_size, data):
    input_layer = Input(input_size)
    preprocessor = Lambda(model_preprocessor)(input_layer)
    base_model = model_name(weights='imagenet', include_top=False, input_shape=input_size)(preprocessor)
    avg = GlobalAveragePooling2D()(base_model)
    feature_extractor = Model(inputs=input_layer, outputs=avg)
    feature_maps = feature_extractor.predict(data)
    return feature_maps

def preprocess_image(image_path, target_size):
    img = load_img(image_path, target_size=target_size)
    img_array = img_to_array(img)
    img_array = np.expand_dims(img_array, axis=0)
    return img_array

def extract_and_combine_features(image_path):
    img_size = (125, 125, 3)
    img = preprocess_image(image_path, target_size=img_size)
    inception_features = get_features(InceptionV3, inception_preprocessor, img_size, img)
    xception_features = get_features(Xception, xception_preprocessor, img_size, img)
    nasnet_features = get_features(NASNetLarge, nasnet_preprocessor, img_size, img)
    inc_resnet_features = get_features(InceptionResNetV2, inc_resnet_preprocessor, img_size, img)
    final_features = np.concatenate([inception_features, xception_features, nasnet_features, inc_resnet_features], axis=-1)
    return img

def predict_breed(image_path):
    features = extract_and_combine_features(image_path)
    prediction = model.predict(features)
    predicted_class = np.argmax(prediction, axis=1)
    return classes[predicted_class[0]]

if __name__ == "__main__":
    image_path = sys.argv[1]  # 이미지 경로는 명령줄 인수로 받음
    predicted_breed = predict_breed(image_path)
    print(f"Predicted breed: {predicted_breed}")
