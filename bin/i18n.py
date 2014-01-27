#!/usr/bin/env python3

import shutil

from common import HOME, load_obj, edit_cls, expr


def main():
    load_obj()

    edit = edit_cls('Label')
    edit.prepare_after_prologue('init')
    edit.find_line(r' invoke-virtual \{v0, p1\}, %s' % expr('$StringBuilder->append()', regex=True), where='down')
    edit.comment_line()
    edit.add_line(r' invoke-static {p1}, %s' % expr('$Translate->t()'))
    edit.add_line(r' move-result-object v1')
    edit.add_line(r' invoke-virtual {v0, v1}, %s' % expr('$StringBuilder->append()'))

    edit.prepare_after_prologue('setText')
    edit.find_line(r' invoke-virtual \{v0, p1\}, %s' % expr('$StringBuilder->append__1()', regex=True), where='down')
    edit.comment_line()
    edit.add_line(r' invoke-static {p1}, %s' % expr('$Translate->t()'))
    edit.add_line(r' move-result-object v1')
    edit.add_line(r' invoke-virtual {v0, v1}, %s' % expr('$StringBuilder->append()'))
    edit.find_line(r' invoke-virtual \{v0, p1\}, %s' % expr('$StringBuilder->append()', regex=True), where='down')
    edit.comment_line()
    edit.add_line(r' invoke-static {p1}, %s' % expr('$Translate->t()'))
    edit.add_line(r' move-result-object v1')
    edit.add_line(r' invoke-virtual {v0, v1}, %s' % expr('$StringBuilder->append()'))
    edit.save()

if __name__ == '__main__':
    main()
