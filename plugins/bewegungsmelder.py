#!/usr/bin/python
 
#Import
import RPi.GPIO as GPIO
import time, datetime, sys, socket



print "BEWEGUNGSMELDER"
print ""
 
#Board Mode: Angabe der Pin-Nummer
GPIO.setmode(GPIO.BOARD)
 
#GPIO Pin definieren fuer den Dateneingang vom Sensor
PIR_GPIO = 7
GPIO.setup(PIR_GPIO,GPIO.IN)
 
read=0
wait=0

sensor=1
clientsocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
try:
    clientsocket.connect(('localhost', 9999))
except socket.error:
    pass


def send(message):
    global clientsocket

    i=0
    while i<3:
        try:
            clientsocket.send(message)
            break

        except socket.error:
            try:
                clientsocket.close()
            except socket.error:
                pass

            clientsocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            try:
                clientsocket.connect(('localhost', 9999))
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
