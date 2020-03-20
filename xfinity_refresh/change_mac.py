from argparse import ArgumentParser
from spoofmac import random_mac_address, set_interface_mac
import subprocess
from subprocess import PIPE
import os
from halo import Halo


def change_mac(interface: str):
    with Halo(text='Disconnecting interface') as s:
        res = subprocess.run(['sudo', 'ifconfig', interface, 'down'], stdout=PIPE, stderr=PIPE)
        if res.returncode:
            s.fail(res.stderr.decode('ascii'))
        else:
            s.succeed('Device {} disconnected'.format(interface))

    with Halo(text='Changing MAC address') as s:
        mac = random_mac_address()
        set_interface_mac(interface, mac)
        s.succeed('Changed MAC address to {}'.format(mac))

    with Halo(text='Connecting interface') as s:
        res = subprocess.run(['sudo', 'ifconfig', interface, 'up'], stdout=PIPE, stderr=PIPE)
        if res.returncode:
            s.warn(res.stderr.decode('ascii'))
        else:
            s.succeed('Device {} connected'.format(interface))


if __name__ == '__main__':
    parser = ArgumentParser()
    parser.add_argument('iface', metavar='interface', type=str)
    args = parser.parse_args()

    change_mac(args.iface)
