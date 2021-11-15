#!/usr/bin/python3
import RPi.GPIO as GPIO, socket, time
import os, sys

if len(sys.argv) != 5:
    print("Invalid number of arguments")
    sys.exit(1)

#GPIO Pin definieren fuer den Dateneingang vom Sensor
ports_config = sys.argv[1]
server_host = sys.argv[2]
server_port = int(sys.argv[3])
success_file = sys.argv[4]

sensors_config = []
for port_config in ports_config.split(','):
    parts = port_config.split(':')
    sensor = int(parts[0])
    pin = int(parts[1])
    sensors_config.append({ 'pin': pin, 'sensor': sensor })

GPIO.setmode(GPIO.BCM)
for sensor in sensors_config:
    GPIO.setup(sensor['pin'], GPIO.IN)

clientsocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
try:
    clientsocket.connect((server_host, server_port))
except socket.error:
    pass



def touch(path):
    with open(path, 'a'):
        os.utime(path, None)

def send(message):
    global clientsocket, server_host, server_port, success_file

    i=0
    while i<3:
        try:
            clientsocket.send(str.encode(message))
            response = str(clientsocket.recv(4096).decode('utf-8')).strip()
            if response == "OK":
                touch(success_file)
            break

        except socket.error:
            try:
                clientsocket.close()
            except socket.error:
                pass

            clientsocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            try:
                clientsocket.connect((server_host, server_port))
            except socket.error:
                pass

        i=i+1
        time.sleep(1)

    if i==3:
        print("error sending message: ", message)

while True:
    for sensor in sensors_config:
        send("observation %s %s %s\n" % (sensor['sensor'], int(time.time()), GPIO.input(sensor['pin'])))

        time.sleep(10)


