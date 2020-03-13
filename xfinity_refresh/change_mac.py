from argparse import ArgumentParser
from spoofmac import random_mac_address, set_interface_mac
import subprocess
import os
from halo import Halo


def change_mac(interface: str):
    with Halo(text='Disconnecting interface') as s:
        res = subprocess.run(['sudo', 'nmcli', 'd', 'disconnect', interface], capture_output=True)
        if res.returncode:
            if 'This device is not active' in res.stderr.decode('ascii'):
                s.succeed(f'Device {interface} already stopped')
            else:
                s.fail(res.stderr.decode('ascii'))
        else:
            s.succeed(f'Device {interface} disconnected')

    with Halo(text='Changing MAC address') as s:
        mac = random_mac_address()
        set_interface_mac(interface, mac)
        s.succeed(f'Changed MAC address to {mac}')

    with Halo(text='Connecting interface') as s:
        res = subprocess.run(['sudo', 'nmcli', 'd', 'connect', interface], capture_output=True)
        if res.returncode:
            s.warn(res.stderr.decode('ascii'))
        else:
            s.succeed(f'Device {interface} connected')


if __name__ == '__main__':
    parser = ArgumentParser()
    parser.add_argument('iface', metavar='interface', type=str)
    args = parser.parse_args()

    change_mac(args.iface)
