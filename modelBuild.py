from __future__ import absolute_import, division, print_function, unicode_literals
import csv
import glob
import tensorflow as tf
import pandas as pd
import numpy as np
import logging
logger = tf.get_logger()
logger.setLevel(logging.ERROR)



gestures = []
labels = []
for filepath in glob.iglob('gestures-dataset/U01/01/*.txt'):
    gest = []
    with open(filepath,'r') as f:
        reader = csv.reader(f, delimiter = ' ')
        for row in reader:
            gest = []
            list = [float(item) for item in row]
            gest.extend(list)

    gestures.append(gest)
    labels.append(1)

for file in glob.iglob('gestures-dataset/U01/02/*.txt'):
    gest = []
    with open(file,'r') as f:
        reader = csv.reader(f, delimiter = ' ')
        for row in reader:
            gest = []
            list = [float(item) for item in row]
            gest.extend(list)

    gestures.append(gest)
    labels.append(2)


ds=pd.DataFrame(gestures,columns=['cT', 'nT','eT','x','y','z'])
dataset = tf.data.Dataset.from_tensor_slices((ds, labels))
print (dataset)

model = tf.keras.Sequential([
    tf.keras.layers.Flatten(input_shape=((6,), ())),
    tf.keras.layers.Dense(6, activation=tf.nn.relu),
    tf.keras.layers.Dense(20,  activation=tf.nn.softmax)
])
model.compile(optimizer='adam',
              loss='sparse_categorical_crossentropy',
              metrics=['accuracy'])
BATCH_SIZE = 32
train_dataset = dataset.repeat().shuffle(40).batch(BATCH_SIZE)
model.fit(train_dataset, epochs=5)
# print(len(gestures))
# print(len(labels))