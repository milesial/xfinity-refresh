#!/usr/bin/env python3

from setuptools import setup
import os

# parse dependencies from requirements.txt
folder = os.path.dirname(os.path.realpath(__file__))
requirement_path = folder + '/requirements.txt'
install_requires = []
if os.path.isfile(requirement_path):
    with open(requirement_path) as f:
        install_requires = f.read().splitlines()

setup(name='xfinity_refresh',
      version='1.0',
      description='xfinity free complimentary pass refresher',
      author='milesial',
      packages=['xfinity_refresh'],
      install_requires=install_requires,
      entry_points={
          'console_scripts': [
              'xfinity-refresh = xfinity_refresh.__main__:main',
              'xfinity-refresh-server = xfinity_refresh.__main__:server',
          ]
      },
     )
