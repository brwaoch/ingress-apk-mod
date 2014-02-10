#!/usr/bin/env python3

import shutil

from common import HOME, load_obj, edit_cls, expr


def main():
    load_obj()

    edit = edit_cls('Label')
    edit.prepare_after_prologue('init')
    edit.find_line(r' invoke-virtual \{v0, p1\}, %s' % expr('$StringBuilder->append()', regex=True), where='down')
    edit.comment_line()
    edit.add_line(r' invoke-static {p0, p1}, %s' % expr('$Translate->t()'))
    edit.add_line(r' move-result-object v1')
    edit.add_line(r' invoke-virtual {v0, v1}, %s' % expr('$StringBuilder->append()'))

    edit.prepare_after_prologue('setText')
    edit.find_line(r' invoke-virtual \{v0, p1\}, %s' % expr('$StringBuilder->append__1()', regex=True), where='down')
    edit.comment_line()
    edit.add_line(r' invoke-static {p0, p1}, %s' % expr('$Translate->t()'))
    edit.add_line(r' move-result-object v1')
    edit.add_line(r' invoke-virtual {v0, v1}, %s' % expr('$StringBuilder->append()'))
    edit.find_line(r' invoke-virtual \{v0, p1\}, %s' % expr('$StringBuilder->append()', regex=True), where='down')
    edit.comment_line()
    edit.add_line(r' invoke-static {p0, p1}, %s' % expr('$Translate->t()'))
    edit.add_line(r' move-result-object v1')
    edit.add_line(r' invoke-virtual {v0, v1}, %s' % expr('$StringBuilder->append()'))
    edit.save()

    edit = edit_cls('SimpleClientPlext')
    edit.find_method_def('getText')
    edit.find_line(r' return-object v0', where='down')
    edit.prepare_to_insert_before()
    edit.add_line(r' invoke-static {p0, v0}, %s' % expr('$Translate->s()'))
    edit.add_line(r' move-result-object v0')
    edit.find_method_def('toString')
    edit.find_line(r' iget-object v3, p0, %s' % expr('$SimpleClientPlext->text'), where='down')
    edit.prepare_to_insert()
    edit.add_line(r' invoke-static {p0, v3}, %s' % expr('$Translate->s()'))
    edit.add_line(r' move-result-object v3')
    edit.save()

    edit = edit_cls('CommStreamAdapter')
    edit.find_method_def('getItem')
    edit.find_line(r' return-object v0')
    edit.prepare_to_insert_before()
#    edit.add_invoke_entry('dump', 'v0')
    edit.save()

if __name__ == '__main__':
    main()
