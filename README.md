# WS3DApp

This is a very first program to use with the new WS3D environment that can be run with:

```
#! /bin/bash
xhost +

XAUTH=`xauth info | grep file | awk '{print $3}'`

docker run --rm -it --name coppelia-sim \
    -e DISPLAY \
    --net=host \
    --device /dev/snd \
    --privileged \
    -v $XAUTH:/root/.Xauthority \
    -p 4011:4011 \
    brgsil/ws3d-coppelia
```

Save this code in a file with the name ws3d.sh and call

```
chmod ugo+x ws3d.sh
```
to make it executable
