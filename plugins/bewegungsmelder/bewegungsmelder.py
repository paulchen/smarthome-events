#!/usr/bin/python
 
#Import
import RPi.GPIO as GPIO
import time, datetime, sys, socket, os



print "BEWEGUNGSMELDER"
print ""
 
#Board Mode: Angabe der Pin-Nummer
GPIO.setmode(GPIO.BOARD)

if len(sys.argv) != 6:
    print "Invalid number of arguments"
    sys.exit(1)

#GPIO Pin definieren fuer den Dateneingang vom Sensor
PIR_GPIO = int(sys.argv[1])
sensor = int(sys.argv[2])
server_host = sys.argv[3]
server_port = int(sys.argv[4])
success_file = sys.argv[5]

GPIO.setup(PIR_GPIO,GPIO.IN)
 
read = 0
wait = 0

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
            clientsocket.send(message)
            response = str(clientsocket.recv(4096)).strip()
            if response == "OK":
                touch(success_file)
            else:
                print "Error submitting message:", message
                print "Response received:", response
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
        print "error sending message: ", message



try:  
 #PIR auslesen
 while GPIO.input(PIR_GPIO)==1:
   read=0
 print "WARTEN auf Bewegung..."

 prev_read = 0
 #Abbruch ctrl+c
 while True : 
   #PIR auslesen
   read = GPIO.input(PIR_GPIO)
   
   if prev_read == 0 and read == 1: 
     send("observation %s %s 1\n" % (sensor, int(time.time())))
     wait = 0
   
   elif prev_read == 1 and read == 1:
     wait = wait + 1
     if wait > 100:
       send("observation %s %s 1\n" % (sensor, int(time.time())))
       wait = 0

   elif prev_read == 1 and read == 0:
     wait = 0

   elif prev_read == 0 and read == 0:
     wait = wait + 1
     if wait > 6000:
       send("observation %s %s 0\n" % (sensor, int(time.time())))
       wait = 0

   sys.stdout.flush()
 
   prev_read = read
   time.sleep(.01)
 
except KeyboardInterrupt:
 print "Beendet"
 GPIO.cleanup()
 clientsocket.close()
