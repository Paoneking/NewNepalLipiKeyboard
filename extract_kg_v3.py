import os
import json

files_to_process = [
    "be.txt", "bg.txt", "bn.txt", "bs.txt", "ca.txt", "cs.txt", "cy.txt", "da.txt", "de.txt", "dv.txt",
    "el.txt", "en.txt", "es.txt", "et.txt", "eu.txt", "fa.txt", "fi.txt", "fr.txt", "gd.txt", "gl.txt",
    "gu.txt", "hi.txt", "hr.txt", "hu.txt", "hy.txt", "id.txt", "is.txt", "it.txt", "ka.txt", "kk.txt",
    "km.txt", "kn.txt", "ko.txt", "lb.txt", "lo.txt", "lt.txt", "lv.txt", "mk.txt", "ml.txt", "mn.txt",
    "mr.txt", "ms.txt", "my.txt", "nb.txt", "ne.txt", "nl.txt", "or.txt", "pa.txt", "pl.txt", "pt.txt"
]

base_path = "app/src/main/assets/locale_key_texts/"

nodes = {}
edges = []

def add_node(node_id, label, node_type, source_file, location):
    if node_id not in nodes:
        nodes[node_id] = {
            "id": node_id,
            "label": label,
            "type": node_type,
            "source_file": source_file,
            "source_location": location
        }

def add_edge(source, target, relation):
    edges.append({
        "source": source,
        "target": target,
        "relation": relation,
        "confidence": "EXTRACTED"
    })

for filename in files_to_process:
    file_path = os.path.join(base_path, filename)
    if not os.path.exists(file_path): continue
    
    locale = filename.replace(".txt", "")
    l_id = f"L_{locale}"
    add_node(l_id, locale, "Locale", file_path, "File")
    
    current_section = None
    section_examples = {}

    try:
        with open(file_path, "r", encoding="utf-8") as f:
            for i, line in enumerate(f):
                line = line.strip()
                if not line or line.startswith("#"): continue
                if line.startswith("[") and line.endswith("]"):
                    current_section = line[1:-1]
                    s_id = f"S_{current_section}"
                    add_node(s_id, current_section, "Module", "Common", "Multiple")
                    add_edge(l_id, s_id, "HAS_MODULE")
                    section_examples[current_section] = 0
                    continue
                
                if current_section and section_examples.get(current_section, 0) < 2:
                    if current_section == "popup_keys":
                        parts = line.split()
                        if len(parts) >= 2:
                            base = parts[0]
                            k_id = f"EK_{locale}_{base}"
                            add_node(k_id, f"Key: {base} (Popups: {' '.join(parts[1:4])})", "KeyExample", file_path, f"L{i+1}")
                            add_edge(l_id, k_id, "DEFINES_KEY_MAPPING")
                            section_examples[current_section] += 1
                    elif current_section == "labels":
                        if ":" in line:
                            k, v = line.split(":", 1)
                            lb_id = f"EL_{locale}_{k.strip()}"
                            add_node(lb_id, f"Label: {k.strip()} -> {v.strip()}", "LabelExample", file_path, f"L{i+1}")
                            add_edge(l_id, lb_id, "DEFINES_LABEL")
                            section_examples[current_section] += 1
    except: pass

result = {"nodes": list(nodes.values()), "edges": edges}
print(json.dumps(result, ensure_ascii=False))
