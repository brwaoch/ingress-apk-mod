#!/usr/bin/env python3

import os
import re
import shutil
import subprocess
import tempfile
from itertools import tee, filterfalse
from PIL import Image

from common import HOME
import texture_unpacker

def main():
    cwd = os.getcwd()
    os.chdir(HOME)
    try:
        repack('', (20, 31, 40, 48))
        repack('-xhdpi', (32, 60, 72, 86))
        repack('-xxhdpi', (50, 85, 103, 121))
    finally:
        os.chdir(cwd)


def repack(name, coda_sizes):
    shutil.rmtree('build/assets/data%s' % name, ignore_errors=True)

    # Create dirs
    for f in 'common', 'packed', 'portal_info', 'upgrade':
        os.makedirs('build/assets/data%s/%s' % (name, f))

    # Copy some files
    for f in 'inconsolata-14.fnt', 'inconsolata-14.png', 'inconsolata-28.fnt', 'inconsolata-28.png':
        shutil.copy('app/assets/common/data%s/%s' % (name, f), 'build/assets/data%s/common' % name)

    shutil.copy('app/assets/common/data%s/nemesis.json' % name, 'build/assets/data%s/common/nemesis.json' % name)

    # Copy upgrade/data*/* images
    if os.path.exists('app/assets/upgrade/data%s' % name):
        for f in os.listdir('app/assets/upgrade/data%s' % name):
            shutil.copy('app/assets/upgrade/data%s/%s' % (name, f), 'build/assets/data%s/upgrade/%s' % (name, f))

    # Repack atlases (replace fonts)
    d = tempfile.mkdtemp()
    texture_unpacker.Unpacker('app/assets/%s/data%s/%s.atlas' % ('packed', name, 'common')).unpack(d, 1)
    # For common.atlas copy fonts
    for size, font_name in zip(coda_sizes, ('x-small', 'sm', 'med', 'lg')):
        shutil.copy('res/fonts/coda-%d.fnt' % size,
                    'build/assets/data%s/common/coda-%s.fnt' % (name, font_name))
#        shutil.copy('res/fonts/coda-%d_0.png' % size, 'build/assets/data%s/common/coda-%s_0.png' % (name, size))
#        if os.path.exists('%s/coda-%s.png' % (d, font_name)):
#            os.remove('%s/coda-%s.png' % (d, font_name))
        shutil.copy('res/fonts/coda-%d_0.png' % size, '%s/coda-%s.png' % (d, font_name))

    shutil.copy('res/lowres/%s-pack.json' % 'common', '%s/pack.json' % d)
    texture_pack(d, 'build/assets/data%s/%s' % (name, 'packed'), 'common')
    shutil.rmtree(d)

    # Copy "magic" portal_ui.atlas
    
    # Repack portal, energy-alien and energy-resistance images only, then readd additional "images" to the atlas file
    shutil.copy('app/assets/portal_info/data%s/portal_ui.atlas' % name, 'build/assets/data%s/portal_info/portal_ui.atlas' % name)
    shutil.copy('app/assets/portal_info/data%s/portal_ui.png' % name, 'build/assets/data%s/portal_info/portal_ui.png' % name)

def texture_pack(in_dir, out_dir, name):
    subprocess.check_call(
        'java -cp lib/gdx.jar:lib/gdx-tools.jar com.badlogic.gdx.tools.imagepacker.TexturePacker2 %s %s %s' % (
            in_dir, out_dir, name), shell=True)


def partition(pred, iterable):
    t1, t2 = tee(iterable)
    return filter(pred, t2), filterfalse(pred, t1)


if __name__ == '__main__':
    main()
