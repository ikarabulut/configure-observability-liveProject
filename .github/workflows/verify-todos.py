#!/usr/bin/env python3

import os
import sys
import re
import yaml


if not os.path.exists("expected-todos.yml") :
    print("TODOS: No expected-todos.yml")
    sys.exit(0)

def execute_shell_command(command) :
    r = os.system(command)
    if r != 0 :
        print(f"non 0 status code {r} for {command}")
        sys.exit(99)

def read_lines_from_file(file):
    f = open(file, "r")
    lines = f.readlines()
    f.close()
    return lines


def failed_tests():
    in_failed_tests = False
    failures = []
    for line in read_lines_from_file("build/reports/todoTestReport/index.html"):
        if in_failed_tests and "<h2>" in line:
            return failures
        elif in_failed_tests and '<a href="' in line and '#' in line:
            m = re.search('<a href="classes/(.*[^.]+).html#([^(]+\(\))">', line)
            failures.append(m.group(1) + "#" + m.group(2))
        elif not in_failed_tests and "<h2>Failed tests</h2>" in line:
            in_failed_tests = True
    return []


execute_shell_command("./gradlew todo")

failures = failed_tests()
yaml_file = "expected-todos.yml"
if os.path.exists(yaml_file):
    with open(yaml_file, 'r') as stream:
        recorded_failures = yaml.safe_load(stream)['failures']
        s1 = set(recorded_failures) - set(failures)
        s2 = set(failures) - set(recorded_failures)
        c = set(failures).intersection(set(recorded_failures))
        if len(s1) > 0 or len(s2) > 0:
            print(f"TODOS: still failing:")
            for s in c:
              print(s)
            if len(s1) > 0:
                print(f"TODOS: missing failures:")
                for s in s1:
                    print(s)
            if len(s2) > 0:
                print(f"TODOS: new failures:")
                for s in s2:
                    print(s)
            print(f"TODOS: new expectations:")
            print (yaml.dump(failures))
            sys.exit(1)
        else:
            print("TODOS: successfully verified")