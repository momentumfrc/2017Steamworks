from networktables import NetworkTables as nt # install networktables with pip install pynewtorktables
import sys
import signal
import time
import argparse

PDPSubsystem = "Robot Power"
PDPName = "PDP"

parser = argparse.ArgumentParser(description="Print and/or log data from the PDP during LiveWindow mode")
parser.add_argument("-k", "--keys", nargs="*", dest="keys",
    choices=["Chan0", "Chan1", "Chan2", "Chan3", "Chan4", "Chan5", "Chan6", "Chan7", "Chan8", "Chan9", "Chan10", "Chan11", "Chan12", "Chan13", "Chan14", "Chan15", "Voltage", "TotalCurrent"],
    help="Which keys from the PDP to output")
parser.add_argument("-l", "--log", default=None, type=argparse.FileType('w'), dest="log", help="File to log data to. See -t")
parser.add_argument("-t", "--type", choices=["csv","txt"], default="csv", dest="type", help="How to format saved data. Defaults to csv")
parser.add_argument("-r", "--rate", default=2, type=float, dest="rate", help="How long to wait in seconds between logging a datapoint. Defaults to 2")
parser.add_argument("--host", default="roboRIO-4999-FRC.local", dest="host", help="The ip address of the host of the networktables server. Usually the roborio. Defaults to roboRIO-4999-FRC.local")
parser.add_argument("--suppress-output", action="store_true", dest="noout", help="Don't log to the console. Only useful when used in addition to -l")

args = parser.parse_args()

c = True

def signal_handler(signal, frame):
    print("Exiting")
    c = False

nt.initialize(args.host);
if not nt.isConnected():
    sys.exit("Could not connect to networktables")

smartdashboard = nt.getTable("SmartDashboard")
livewindow = nt.getTable("LiveWindow")
pdp = nt.getSubtable(PDPSubsystem).getSubtable(PDPName)

signal.signal(signal.SIGINT, signal_handler)

skipkeys = ["~TYPE~","Name","Subsystem"]

if args.log is not None and args.type == "csv":
    keys = pdp.getKeys()
    for key in keys:
        if (key in skipkeys) or (args.keys is not None and key not in args.keys):
            continue
        args.log.write('"%s,"'%key)
    args.log.write("\n")

while c:
    keys = pdp.getKeys()
    out = ""
    for key in keys:
        if (key in skipkeys) or (args.keys is not None and key not in args.keys):
            continue
        if not args.noout:
            print("%s: %s" % (key, pdp.getNumber(key, -1)))
        if args.log is not None:
            if args.type == "csv":
                out += (pdp.getNumber(key, -1) + ",")
            else:
                out += "%s: %s\n" % (key, pdp.getNumber(key, -1))
    if not args.noout:
        print("\n\n")
    if args.log is not None:
        if args.type == "csv":
            out.rstrip(',')
            out += "\n"
        args.log.write(out)
    time.sleep(args.rate)
