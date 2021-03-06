#!/bin/bash

# This is a sample script to demonstrate 
# GUITAR general workflow 
# The output can be found in Demo directory  

#------------------------
# application directory 
# aut_dir=$SCRIPT_DIR/jfc-aut/RadioButton/

# application classpath 
# aut_classpath=$aut_dir/bin

# application main class
# mainclass="Project"

# Change the following 2 lines for the classpath and the main class of your 
# application. The example is for CrosswordSage, another real world example
# in the jfc-aut directory (http://crosswordsage.sourceforge.net/)

#aut_classpath=$SCRIPT_DIR/jfc-aut/CrosswordSage/bin:$SCRIPT_DIR/jfc-aut/CrosswordSage/bin/CrosswordSage.jar
#mainclass="crosswordsage.MainScreen"

#------------------------
# GLOBAL VARIABLES
#------------------------
# Sample command line arguments 
SCRIPT_DIR=`dirname $0`
aut_classpath=.

#aut_directory=TippyTipper
#aut_package=net.mandaria.tippytipper
#aut_main=activities.TippyTipper

aut_directory=$1
aut_package=$2
aut_main=$3
RESULT_DIR=$4
AUT_DISPLAY=$5
aut=$aut_package.$aut_main
port=10737

args=""

# configuration for the application
# you can specify widgets to ignore during ripping 
# and terminal widgets 
# configuration="$aut_dir/guitar-config/configuration.xml"

# intial waiting time
# change this if your application need more time to start
intial_wait=3000

# delay time between two events during ripping 
ripper_delay=500

# the length of test suite
tc_length=2

# delay time between two events during replaying  
# this number is generally smaller than the $ripper_delay
replayer_delay=1000

#------------------------
# Output artifacts 
#------------------------

# Directory to store all output of the workflow 
output_dir="./Demo"

# GUI structure file
gui_file="$output_dir/Demo.GUI"

# EFG file 
efg_file="$output_dir/Demo.EFG"

# Log file for the ripper 
# You can examine this file to get the widget 
# signature to ignore during ripping 
log_file="$output_dir/Demo.log"

# Test case directory  
testcases_dir="$output_dir/testcases"

# GUI states directory  
states_dir="$output_dir/states"

# Replaying log directory 
logs_dir="$output_dir/logs"

#------------------------
# Main workflow 
#------------------------

# this function set ups workspace
_envr_setup()
{
    # turn off posix mode
    set +o posix
    
    # Preparing output directories
    mkdir -p $output_dir
    mkdir -p $testcases_dir
    mkdir -p $states_dir
    mkdir -p $logs_dir
}

# this function downloads and installs android tools
_installAndroidSDK()
{
    echo "    We will try to install android 2.2 and sdk tools."
            
    echo -e "==> Download Android SDK"
    downloader=$(which wget)
    if [ $downloader ]; then
            wget http://dl.google.com/android/android-sdk_r22.0.1-linux.tgz
    else
            curl http://dl.google.com/android/android-sdk_r22.0.1-linux.tgz > android-sdk_r22.0.1-linux.tgz
    fi

    cp android-sdk_r22.0.1-linux.tgz $HOME
    rm android-sdk_r22.0.1-linux.tgz
    cd $HOME

    echo -e "==> Uncompress Android SDK"
    tar xvfz android-sdk_r22.0.1-linux.tgz
    
    echo -e "==> Exporting sdk paths"
    tools_path="$HOME/android-sdk-linux/tools:$HOME/android-sdk-linux/platform-tools"
    original_PATH=${PATH}
    export PATH=${PATH}:${tools_path}
    echo "y" | android update sdk -u -t 1,2,12
}

# this function checks if android 2.2 exists
_checkAndroid2.2()
{
    id=100
    if [[ `android list targets | grep "android-8"` =~ [0-9]+ ]]; then
        id=${BASH_REMATCH[0]}
    fi
    
    if [[ id -ne 100 ]]; then
        echo -e "==> Android 2.2 exists, if build fails after this, try manually installing build-tools and platform-tools."
    else
        echo -e "==>    Android 2.2 is not installed."
        _installAndroidSDK
    fi
}

# this function set ups android sdks and tools
_androidSDK_setup()
{
    # Ripping
    found=$(which android)
    if [ $found ]; then
        echo -e "==> Android SDK Exists. We will skip download and test if android 2.2 is installed."
        
        _checkAndroid2.2
    else
        echo -e "==> Android not found in the PATH."
    
        if [ -d $HOME/android-sdk-linux/ ]; then
            echo -e "==> Found android folder under HOME. We skip download and test if tools exist."
            echo -e "==> Exporting sdk paths"
            
            tools_path="$HOME/android-sdk-linux/tools:$HOME/android-sdk-linux/platform-tools"
            original_PATH=${PATH}
            export PATH=${PATH}:${tools_path}
            
             id=100
             if [[ `android list targets | grep "android-8"` =~ [0-9]+ ]]; then
                    id=${BASH_REMATCH[0]}
             fi
    
             if [[ id -ne 100 ]]; then
                    echo -e " Android 2.2 exists, if build fails after this, try manually installing build-tools and platform-tools."
             else
                echo -e "Android 2.2 doesn't exist. Try installing it along with build-tools and platform-tools manually."
                exit
             fi
        else
            _installAndroidSDK
        fi
    fi
}

# this function set ups emulator
_emulator_setup()
{
    echo -e "==> Kill the emulator process if running.\n"
    pkill emulator-arm
    killall emulator-arm
    
    echo "==> Delete the AVD if its name is ADRGuitarTest."
    android delete avd -n ADRGuitarTest
    echo -e "\n"
    
    echo "==> Create an AVD. Its name will be ADRGuitarTest."
    echo | android create avd -n ADRGuitarTest -t $id -s WQVGA432
    
    # ADR-Server Building
    echo -e "==> Build ADR-Server"
    cd adr-aut
    rm -f adr-server.apk
    cd adr-server
    
    android update project --name adr-server --target android-8 --path .
    python rename.py AndroidManifest.xml $aut_package
    ant debug
    ../resign.sh ./bin/adr-server-debug.apk ../adr-server.apk
    cd ../../
    
    # AUT Building
    echo -e "==> Build AUT"
    cd adr-aut/$aut_directory
    
    rm build.xml
    android update project --target android-8 -p .
    cp -rf src src.orig
    if [ ! -f ./bin/no_fault/aut-resigned.apk ] || [ ! -f ./bin/no_fault/coverage.em ]; then
        ant instrument
        ../resign.sh ./bin/*-instrumented.apk ./bin/aut-resigned.apk
        mkdir -p ./bin/no_fault
        cp ./bin/aut-resigned.apk ./bin/no_fault
        # Changes, added ./bin/ to cp
        cp ./bin/coverage.em ./bin/no_fault
        # End Changes
    fi
    # Changes
    echo -e "Current Dir:"
    pwd
    # End Changes
    while read line
    do
        filename=${line##*/}
        pathname=${line%/*}
        original_path=`find src -name $filename`
        if [ ! -f ./bin/$pathname/aut-resigned.apk ] || [ ! -f ./bin/$pathname/coverage.em ]; then
            echo "==> Fault seeded source file: $line"
            cp $line $original_path
            ant instrument
            ../resign.sh ./bin/*-instrumented.apk ./bin/aut-resigned.apk
            mkdir -p ./bin/$pathname
            cp ./bin/aut-resigned.apk ./bin/$pathname
            # Change, added ./bin/ to cp
            pwd
            cp ./bin/coverage.em ./bin/$pathname
            # End Changes
        fi
     
    done < <( find $RESULT_DIR -name *.java )
    rm -rf src
    mv src.orig src
    rm -rf bin/*.apk
    cd ../../
    
    # Run an emulator process
    echo -e "==> Run the created emulator.\n"
    emulator -avd ADRGuitarTest -cpu-delay 0 -netfast $AUT_DISPLAY -noaudio -no-snapshot-save &
}

# this function connects to emulator display
_install_adr-server()
{
    # Install ADR-Server
    echo "==> Install ADR-Server."
    cont=true
    while $cont ;
    do
        while read line
        do
            found=$(echo $line | grep Success)
            if [ "$found" ]; then
                cont=false
            fi
    #    done < <( adb install adr-server/adr-server.apk )
        done < <( adb install adr-aut/adr-server.apk )
    
        if $cont ; then
            echo "  The emulator is booting."
            echo "  We will retry."
            adb kill-server
            adb start-server
        fi
        sleep 10
    done
    echo -e "\n"
}

# this function installs test source files
_install_aut()
{
    apk_file=`find adr-aut/$aut_directory -name *.apk | sed -n '/no_fault/p'`
    
    # Install AUT
    echo "==> Install AUT: $apk_file"
    cont=true
    while $cont ;
    do
        while read line
        do
            found=$(echo $line | grep Success)
            if [ "$found" ]; then
                cont=false
            fi
        done < <( adb install $apk_file )
    
        if $cont ; then
            echo "    The emulator is booting."
            echo "    We will retry."
        fi
        sleep 10
    done
    echo -e "\n"
}

# this function calls ripper to rip the app GUI
_adr-ripper()
{
    echo "About to rip the application " 
    #read -p "Press ENTER to continue..."
    cmd="$SCRIPT_DIR/adr-ripper.sh -cp $aut_classpath -c $aut -pt $port -g $gui_file -l $log_file"
    
    # Addng application arguments if needed 
    if [ ! -z $args ] 
    then 
        cmd="$cmd -a \"$args\"" 
    fi
    echo $cmd
    eval $cmd
}

# this function calls gui2efg.sh to convert gui.xml to efg.xml
_gui2efgConverter()
{
    # Converting GUI structure to EFG
    echo ""
    echo "About to convert GUI structure file to Event Flow Graph (EFG) file" 
    #read -p "Press ENTER to continue..."
    cmd="$SCRIPT_DIR/gui2efg.sh -g $gui_file -e $efg_file"
    echo $cmd
    eval $cmd
}
 
# this function generates testcases based on testcase_num
_testcases_generator()
{
    # Generating test cases
    echo ""
    echo "About to generate test cases to cover all possible $tc_length-way event interactions" 
    #read -p "Press ENTER to continue..."
    rm -rf $testcases_dir
    cmd="$SCRIPT_DIR/tc-gen-sq.sh -e $efg_file -l $tc_length -m 0 -d $testcases_dir"
    echo $cmd
    eval $cmd 
    
    testcase_num=`find Demo/testcases/*.tst | wc -l`
    # Can change to desired number of testcases, generated nums at max
    testcase_num=2
    rm -rf `find adr-aut/$aut_directory -name '*.res'`
    rm -rf `find adr-aut/$aut_directory -name '*.ec'`
    rm -rf `find adr-aut/$aut_directory -name '*.log'`
}

# this function replays generated testcases
_testcases_replayer()
{
    # Replaying generated test cases
    echo ""
    echo "About to replay test case(s)" 
    echo "Enter the number of test case(s): $testcase_num"
    
    rip_minute=`awk '$7=="Elapsed:" { print $10 }' $log_file | sed 's/^0*//'`
    rip_second=`awk '$7=="Elapsed:" { print $12}' $log_file | sed -n 's/:/ /p' | sed 's/^0*//'`
    
    let rip_second=rip_minute*60+rip_second
    let rip_second=rip_second*1000*3
    
    for testcase in `find $testcases_dir -name "*.tst"| head -n$testcase_num`  
    do
        # getting test name 
        test_name=`basename $testcase`
        test_name=${test_name%.*}
    
        rm -rf $logs_dir/$test_name.log
    
        cmd="$SCRIPT_DIR/adr-replayer.sh -cp $aut_classpath -c $aut -pt $port -g $gui_file -e $efg_file -t $testcase -i $intial_wait -d $replayer_delay -to $rip_second -so $rip_second -l $logs_dir/$test_name.log -gs $states_dir/$test_name.sta"
        
        # Changes
        cmd="$SCRIPT_DIR/adr-replayer.sh -cp $aut_classpath -c $aut -pt $port -g $gui_file -e $efg_file -t $testcase -i $intial_wait -d $replayer_delay -to 60000000 -so 10000000 -l $logs_dir/$test_name.log -gs $states_dir/$test_name.sta"
    
        # adding application arguments if needed 
        if [ ! -z $args ] 
        then 
            cmd="$cmd -a \"$args\" " 
        fi
    
        if [ -f adr-aut/$aut_directory/bin/no_fault/$test_name.res ]; then
            echo "This testcase was already run so we will skip"
        else
            echo $cmd 
            eval $cmd
    
            while [ ! -f adr-aut/$aut_directory/bin/no_fault/$test_name.ec ]
            do
                adb pull /data/data/$aut_package/files/coverage.ec adr-aut/$aut_directory/bin/no_fault/$test_name.ec
                sleep 2
            done
    
            emma_dir=`which android`
            emma_dir=${emma_dir%/*}
            emma_dir=${emma_dir}/lib/emma.jar
            java -cp $emma_dir emma report -r xml -in adr-aut/$aut_directory/bin/no_fault/coverage.em,adr-aut/$aut_directory/bin/no_fault/$test_name.ec
            java -cp $emma_dir emma report -r html -in adr-aut/$aut_directory/bin/no_fault/coverage.em,adr-aut/$aut_directory/bin/no_fault/$test_name.ec
            mv coverage $test_name
            rm -rf adr-aut/$aut_directory/bin/no_fault/$test_name
            mv -f $test_name -t adr-aut/$aut_directory/bin/no_fault/
            mv coverage.xml adr-aut/$aut_directory/bin/no_fault/$test_name/
            cp ${logs_dir}/${test_name}.log adr-aut/$aut_directory/bin/no_fault/
            cp $states_dir/$test_name.sta adr-aut/$aut_directory/bin/no_fault/
        fi
    done
}

main()
{
    # function calls
    _envr_setup
    _androidSDK_setup
    _emulator_setup
    _install_adr-server
    _install_aut
    _adr-ripper
    _gui2efgConverter
    _testcases_generator
    _testcases_replayer
    
    python cv_rpt.py adr-aut/$aut_directory/bin/no_fault
    python ft_rpt.py adr-aut/$aut_directory/bin/result
    cp -rf *.html adr-aut/$aut_directory

    # cleanup
    pkill emulator-arm
    killall emulator-arm
    android delete avd -n ADRGuitarTest
    export PATH=${original_PATH}
}

main "$@"

# This part is commented out.
: <<'END'
while read line
do
    pathname=${line%/*}
    tested=1

    # Check this apk was already tested
    for testcase in `find $testcases_dir -name "*.tst"| head -n$testcase_num`  
    do
        # getting test name 
        test_name=`basename $testcase`
        test_name=${test_name%.*}

        if [ ! -f $pathname/$test_name.res ]; then
            tested=0
            break
       	fi
    done

    [ $tested -eq 1 ] && continue

    # Install AUT
    echo "==> Install AUT: $line"
    cont=true
    while $cont ;
    do
        adb uninstall $aut_package
        while read line
        do
            found=$(echo $line | grep Success)
            if [ "$found" ]; then
                cont=false
            fi
        done < <( adb install $line )

        if $cont ; then
            echo "    The emulator is booting."
            echo "    We will retry."
        fi
        sleep 10
    done
    echo -e "\n"

    # Replaying generated test cases
    echo ""
    echo "About to replay test case(s)" 
    echo "Enter the number of test case(s): $testcase_num"

    for testcase in `find $testcases_dir -name "*.tst"| head -n$testcase_num`  
    do
        # getting test name 
        test_name=`basename $testcase`
        test_name=${test_name%.*}

        rm -rf $logs_dir/$test_name.log

        cmd="$SCRIPT_DIR/adr-replayer.sh -cp $aut_classpath -c $aut -pt $port -g $gui_file -e $efg_file -t $testcase -i $intial_wait -d $replayer_delay -to $rip_second -so $rip_second -l $logs_dir/$test_name.log -gs $states_dir/$test_name.sta"
        
            # Changes
    cmd="$SCRIPT_DIR/adr-replayer.sh -cp $aut_classpath -c $aut -pt $port -g $gui_file -e $efg_file -t $testcase -i $intial_wait -d $replayer_delay -to 60000000 -so 10000000 -l $logs_dir/$test_name.log -gs $states_dir/$test_name.sta"

        # adding application arguments if needed 
        if [ ! -z $args ] 
        then 
            cmd="$cmd -a \"$args\" " 
        fi

        if [ -f $pathname/$test_name.res ]; then
            echo "This testcase was already run so we will skip"
        else
            echo $cmd 
            eval $cmd

            cp $states_dir/$test_name.sta $pathname/
	    rm -rf $states_dir/$test_name.sta
            fault_found=`cat ${logs_dir}/${test_name}.log | grep "NORMALLY TERMINATED" | sed -e 's;^.* ;;'`
	    fault_found2=`diff  $pathname/$test_name.sta adr-aut/$aut_directory/bin/no_fault/$test_name.sta`
            if [[ $fault_found ]] && [[ ! $fault_found2 ]]; then
                echo success > $pathname/$test_name.res

                while [ ! -f $pathname/$test_name.ec ]
                do
                    adb pull /data/data/$aut_package/files/coverage.ec $pathname/$test_name.ec
                    sleep 2
                done

                emma_dir=`which android`
                emma_dir=${emma_dir%/*}
                emma_dir=${emma_dir}/lib/emma.jar
                java -cp $emma_dir emma report -r xml -in $pathname/coverage.em,$pathname/$test_name.ec
                java -cp $emma_dir emma report -r html -in $pathname/coverage.em,$pathname/$test_name.ec
                mv coverage $test_name
                rm -rf $pathname/$test_name
                mv -f $test_name -t $pathname/
                mv coverage.xml $pathname/$test_name/
                cp ${logs_dir}/${test_name}.log $pathname/
            else
               echo fail > $pathname/$test_name.res
            fi
        fi
    done
done < <( find adr-aut/$aut_directory/bin/result -name *.apk | sed '/original/d' )
END
