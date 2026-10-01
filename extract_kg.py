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

nodes = []
edges = []
seen_nodes = set()

def add_node(node_id, label, node_type, source_file, location):
    if node_id not in seen_nodes:
        nodes.append({
            "id": node_id,
            "label": label,
            "type": node_type,
            "source_file": source_file,
            "source_location": location
        })
        seen_nodes.add(node_id)

def add_edge(source, target, relation):
    edges.append({
        "source": source,
        "target": target,
        "relation": relation,
        "confidence": "EXTRACTED"
    })

for filename in files_to_process:
    file_path = os.path.join(base_path, filename)
    if not os.path.exists(file_path):
        continue
    
    locale = filename.replace(".txt", "")
    locale_node_id = f"locale_{locale}"
    add_node(locale_node_id, locale, "Locale", file_path, "entire file")
    
    current_section = None
    try:
        with open(file_path, "r", encoding="utf-8") as f:
            lines = f.readlines()
            for i, line in enumerate(lines):
                line = line.strip()
                if not line:
                    continue
                if line.startswith("[") and line.endswith("]"):
                    current_section = line[1:-1]
                    section_node_id = f"section_{current_section}"
                    add_node(section_node_id, current_section, "Section", file_path, f"line {i+1}")
                    add_edge(locale_node_id, section_node_id, "HAS_SECTION")
                    continue
                
                if current_section == "popup_keys":
                    parts = line.split()
                    if len(parts) >= 2:
                        base_key = parts[0]
                        popups = parts[1:]
                        group_id = f"popup_{locale}_{base_key}"
                        add_node(group_id, f"Popup Group: {base_key}", "PopupGroup", file_path, f"line {i+1}")
                        add_edge(locale_node_id, group_id, "PROVIDES_POPUPS")
                        
                        base_char_id = f"char_{base_key}"
                        add_node(base_char_id, base_key, "Character", file_path, f"line {i+1}")
                        add_edge(group_id, base_char_id, "BASE_KEY")
                        
                        for p in popups:
                            if p.startswith("!icon/"):
                                p_label = p.split("|")[-1] if "|" in p else p
                                p_id = f"icon_{p}"
                                add_node(p_id, p_label, "Icon", file_path, f"line {i+1}")
                            else:
                                p_id = f"char_{p}"
                                add_node(p_id, p, "Character", file_path, f"line {i+1}")
                            add_edge(group_id, p_id, "HAS_POPUP_OPTION")
                
                elif current_section == "labels":
                    if ":" in line:
                        key, val = line.split(":", 1)
                        key = key.strip()
                        val = val.strip()
                        label_node_id = f"label_{locale}_{key}"
                        add_node(label_node_id, f"{key}: {val}", "Label", file_path, f"line {i+1}")
                        add_edge(locale_node_id, label_node_id, "DEFINES_LABEL")
                
                elif current_section == "number_row":
                    chars = line.split()
                    for c in chars:
                        char_id = f"char_{c}"
                        add_node(char_id, c, "Character", file_path, f"line {i+1}")
                        add_edge(locale_node_id, char_id, "HAS_NUMBER_ROW_CHAR")

    except Exception as e:
        pass

print(json.dumps({"nodes": nodes, "edges": edges}))
