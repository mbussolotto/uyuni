{{- define "deployment.container.image" -}}
{{- $imageName := .name -}}
{{- $uri := (printf "%s/%s:%s" .global.Values.repository $imageName .global.Values.version) | default .global.Chart.AppVersion -}}
{{- if .global.Values.images -}}
{{- $image := (get .global.Values.images $imageName) -}}
{{- if $image -}}
{{- $uri = $image -}}
{{- end -}}
{{- end -}}
{{- $uri -}}
{{- end -}}

{{/* uyuni.image computes the image URL out of the global registry and tag as well as overridden values. */}}
{{/*   "name", the image name */}}
{{/*   "global", the root object */}}
{{/*   "local", the configuration object containing the image and tag to override with */}}
{{- define "uyuni.image" -}}
{{- $tag := .global.Values.tag -}}
{{- if .local.tag -}}
{{- $tag = .local.tag -}}
{{- end -}}
{{- $uri := (printf "%s/%s:%s" .global.Values.repository .name $tag) -}}
{{- if .local.image -}}
{{- $uri = (printf "%s:%s" .local.image $tag) -}}
{{- end -}}
{{- $uri -}}
{{- end -}}

{{- define "uyuni.nodePlacement" -}}
{{- $globalValues := .global.Values -}}
{{- $globalNode := $globalValues.global | default (dict) -}}
{{- $localNode := .local | default (dict) -}}

{{- /* 1. Handle nodeSelector */ -}}
{{- $nodeSelector := $globalNode.nodeSelector -}}
{{- if $localNode.nodeSelector }}{{ $nodeSelector = $localNode.nodeSelector }}{{ end -}}
{{- if $nodeSelector }}
nodeSelector:
{{- toYaml $nodeSelector | nindent 2 }}
{{- end }}

{{- /* 2. Handle affinity */ -}}
{{- $affinity := $globalNode.affinity -}}
{{- if $localNode.affinity }}{{ $affinity = $localNode.affinity }}{{ end -}}
{{- if $affinity }}
affinity:
{{- toYaml $affinity | nindent 2 }}
{{- end }}

{{- /* 3. Handle tolerations */ -}}
{{- $tolerations := $globalNode.tolerations -}}
{{- if $localNode.tolerations }}{{ $tolerations = $localNode.tolerations }}{{ end -}}
{{- if $tolerations }}
tolerations:
{{- toYaml $tolerations | nindent 2 }}
{{- end }}

{{- /* 4. Handle nodeName */ -}}
{{- $nodeName := $globalNode.nodeName -}}
{{- if $localNode.nodeName }}{{ $nodeName = $localNode.nodeName }}{{ end -}}
{{- if $nodeName }}
nodeName: {{ $nodeName | quote }}
{{- end }}

{{- end -}}

{{- define "uyuni.hostAliases" -}}
{{- $globalValues := .global.Values -}}
{{- $globalNode := $globalValues.global | default (dict) -}}
{{- $localNode := .local | default (dict) -}}

{{- $hostAliases := $globalNode.hostAliases -}}
{{- if $localNode.hostAliases }}{{ $hostAliases = $localNode.hostAliases }}{{ end -}}
{{- if $hostAliases }}
hostAliases:
{{- toYaml $hostAliases | nindent 2 }}
{{- end }}

{{- end -}}
