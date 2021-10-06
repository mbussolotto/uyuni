{% if pillar['virt_entitled'] %}
/etc/salt/minion.d/libvirt-events.conf:
  file.managed:
    - template: jinja
    - contents: |
        engines:
          - libvirt_events

/var/cache/virt_state.cache:
  file.absent

{% else %}

/etc/salt/minion.d/libvirt-events.conf:
  file.absent

{% endif %}
