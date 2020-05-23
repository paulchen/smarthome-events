# Smarthome Events

Framework designed to capture events within a distributed environment.
Originally developed to record the state of various sensors within a smart home.

It consists of a _server_ that keeps track of all _sensors_ in a database.
It listens to TCP connections from _clients_ running certain _plugins_.
These plugins submit their _observations_ which the server records in its database.

The provides plugins for Icinga and Munin.

## Setup

In order to operate the framework, you need to set up a central server and a set of clients.

### Server

Running the central server requires you to set up a MySQL/MariaDB database, to configure the server, and to create a Systemd unit.

#### Database

Create a MySQL/MariaDB database, configure a user having full permissions, and execute `misc/database.sql` to create the database tables.

Fill the database tables `sensors` with the sensors that you will have in your setup.
You may add additional sensors later when needed.

#### Actual server

You will need JDK 11 or higher and Maven.

Compile the server using `mvn clean install appassembler:assemble`. 

Copy `events.properties.dist` to `events.properties` and edit the file accordingly.

Copy `misc/smarthome-events.service` to `/etc/systemd/system` and edit it to your needs.
Then run:
* `systemctl daemon-reload`
* `systemctl enable smarthome-events`
* `systemctl start smarthome-events`

Consult syslog to check for problems and correct them.

#### Monitoring

This application provides plugins for Icinga (and compatible monitoring frameworks) and Munin.

##### Icinga

Use `check-smarthome` and `check-metrics` from the `misc` directory.
They feature the output from the protocol commands `STATUS` and `METRICS`, respectively.

##### Munin

Use the script `munin` from the `misc` directory. It simply issues the `munin` command to the server and returns its output. 

Keep in mind that at least in Debian, the `munin` unit of Systemd restricts access to certain directories (e.g. `/home`) by default.
Therefore, placing the application somewhere in `/home` and creating a symlink to the plugin from `/etc/munin/plugins` will not work. 

### Clients

Every client runs a certain set of plugins. They are implemented in Python 3.

#### Plugins

The plugins can be found in the `plugins` directory.
Each plugin consists of the plugin script itself and a Systemd unit.
Copy the `.service` file to `/etc/systemd/system` and modify it to your needs.
Finally, enable and run the plugin in the same way as done previously for the server.

Each plugin regularly touches a so-called _success file_ if everything is fine.
You can use Icinga to check whether the timestamp of this file is up-to-date.

##### Bewegungsmelder

Intended to use a PIR motion sensor connected to the GPIO pins of a Raspberry Pi to record whether this sensor records some motion
(the German word `Bewegungsmelder` translates to `motion sensor`).

Parameters:
* GPIO pin
* Server-side sensor id (database table `sensor`).
* Host name or IP address of the server.
* Port of the server.
* Path to the success file.

##### Fenster

Intended to use the GPIO pins of a Raspberry Pi to record the state of reed sensors reflecting the opening states of windows
(the German word _Fenster_ translates to _window_).

Parameters:
* Sensor configuration. Comma-separated list of key-value pairs.
  Keys and values are separated by a colon.
  The key is the server-side id of the sensor (database table `sensor`).
  The value is the GPIO pin. 
* Host name or IP address of the server.
* Port of the server.
* Path to the success file.

## Protocol

The protocol features a set of commands that are sent from a client to the server.
Currently, all data is sent in plain-text, there is no authentication.

### Commands

The following section describes all commands that are currently supported. 

#### PING

Simply replies with `PONG`. No arguments.

#### OBSERVATION

Submits an observation.
Replies with `OK` if the observation was processed successfully.
Replies with `NOK` otherwise.

Arguments:
 * Sensor id as given in the database table `observation`.
 * UNIX timestamp.
 * Sensor value.

Example request:

`OBSERVATION 1 1590247140 test`

#### STATUS

Provides data to the `check-status.sh` script. No arguments.

#### METRICS

Provides data to the `check-metrics.sh` script. No arguments.

#### MUNIN

Provides data to the Munin plugin (described above).
Follows the protocol for Munin plugins.
