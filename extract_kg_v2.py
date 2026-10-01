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
    if not os.path.exists(file_path):
        continue
    
    locale = filename.replace(".txt", "")
    locale_id = f"L_{locale}"
    add_node(locale_id, locale, "Locale", file_path, "File")
    
    current_section = None
    try:
        with open(file_path, "r", encoding="utf-8") as f:
            lines = f.readlines()
            for i, line in enumerate(lines):
                line = line.strip()
                if not line or line.startswith("#"): continue
                if line.startswith("[") and line.endswith("]"):
                    current_section = line[1:-1]
                    section_id = f"S_{current_section}"
                    add_node(section_id, current_section, "Module", "Common", "Multiple")
                    add_edge(locale_id, section_id, "HAS_MODULE")
                    continue
                
                if current_section == "popup_keys":
                    parts = line.split()
                    if len(parts) >= 2:
                        base = parts[0]
                        base_id = f"C_{base}"
                        add_node(base_id, base, "Character", file_path, f"L{i+1}")
                        add_edge(locale_id, base_id, "HAS_KEY")
                        
                        for p in parts[1:4]: # Limit popups to 3 to save space
                            if p.startswith("!"): continue
                            p_id = f"C_{p}"
                            add_node(p_id, p, "Character", file_path, f"L{i+1}")
                            add_edge(base_id, p_id, f"POPUP_{locale}")
                            
                elif current_section == "labels":
                    if ":" in line:
                        k, v = line.split(":", 1)
                        k = k.strip()
                        label_id = f"LB_{locale}_{k}"
                        add_node(label_id, f"{k}: {v.strip()}", "Label", file_path, f"L{i+1}")
                        add_edge(locale_id, label_id, "DEFINES")
    except: pass

result = {"nodes": list(nodes.values()), "edges": edges}
with open("kg_output.json", "w", encoding="utf-8") as f:
    json.dump(result, f, ensure_ascii=False)
