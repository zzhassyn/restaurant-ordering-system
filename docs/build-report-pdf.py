#!/usr/bin/env python3
"""Builds docs/REPORT.pdf from docs/REPORT.md with a headless Chrome or Edge.

Usage:  python docs/build-report-pdf.py

Only the Python standard library is needed. Screenshots are taken from
docs/screenshots/ using the file names given in REPORT.md; missing ones are
shown as placeholders in the PDF.
"""

import html
import os
import re
import shutil
import struct
import subprocess
import sys
import tempfile
from pathlib import Path

DOCS = Path(__file__).resolve().parent
SOURCE = DOCS / "REPORT.md"
TARGET = DOCS / "REPORT.pdf"
SCREENSHOTS = DOCS / "screenshots"
REPO_URL = "https://github.com/zzhassyn/restaurant-ordering-system/blob/main/docs/"

BROWSERS = [
    r"C:\Program Files\Google\Chrome\Application\chrome.exe",
    r"C:\Program Files (x86)\Google\Chrome\Application\chrome.exe",
    r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe",
    r"C:\Program Files\Microsoft\Edge\Application\msedge.exe",
    "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
    "/Applications/Microsoft Edge.app/Contents/MacOS/Microsoft Edge",
    "google-chrome",
    "chromium",
    "chromium-browser",
    "microsoft-edge",
]

CSS = """
@page {
  size: A4;
  margin: 18mm 17mm 20mm 17mm;
  @bottom-center { content: counter(page); font: 9pt "Segoe UI", Arial, sans-serif; color: #777; }
}
@page :first { @bottom-center { content: none; } }
@page landscape { size: A4 landscape; margin: 12mm 12mm 16mm 12mm; }

body { font: 10.5pt/1.5 "Segoe UI", Calibri, Arial, sans-serif; color: #1f2328; }
h1, h2, h3 { color: #12355b; line-height: 1.25; break-after: avoid; }
h2 { font-size: 18pt; border-bottom: 2px solid #12355b; padding-bottom: 4px; margin-top: 0; }
h3 { font-size: 13.5pt; margin-top: 18px; }
p, li { orphans: 3; widows: 3; }
p:has(+ pre), p:has(+ figure), p:has(+ .placeholder) { break-after: avoid; }
a { color: #0b5cad; text-decoration: none; }
code { font: 9pt Consolas, "Courier New", monospace; background: #eef1f4; padding: 0 3px; border-radius: 3px; }
pre {
  font: 8.4pt/1.4 Consolas, "Courier New", monospace; white-space: pre-wrap;
  background: #f6f8fa; border: 1px solid #d8dee4; border-radius: 4px; padding: 8px 10px;
}
pre code { background: none; padding: 0; font: inherit; }
pre.console { background: #1e1e1e; color: #e0e0e0; border-color: #1e1e1e; break-inside: avoid; }
.tok-keyword { color: #8250df; font-weight: 600; }
.tok-string { color: #0a6e31; }
.tok-comment { color: #6e7781; font-style: italic; }
.tok-annotation { color: #9a6700; }
table { border-collapse: collapse; width: 100%; margin: 10px 0; break-inside: avoid; }
th, td { border: 1px solid #d0d7de; padding: 4px 8px; text-align: left; vertical-align: top; }
th { background: #eaf0f6; }
figure { margin: 12px 0; text-align: center; break-inside: avoid; }
figure img { max-width: 100%; max-height: 225mm; }
figure.wide { page: landscape; }
figure.wide img { max-height: 165mm; }
figcaption, .caption { font-size: 9.5pt; color: #57606a; font-style: italic; text-align: center; }
blockquote { margin: 10px 0; padding: 6px 12px; border-left: 4px solid #d0d7de; color: #57606a; }
.placeholder {
  margin: 10px 0 18px; padding: 28px 12px; text-align: center; color: #9a6700;
  border: 2px dashed #d4a72c; background: #fff8e5; border-radius: 6px; break-inside: avoid;
}
.page-break { break-after: page; }

.cover { break-after: page; text-align: center; padding-top: 55mm; }
.cover h1 { font-size: 30pt; margin-bottom: 6px; }
.cover p { font-size: 15pt; color: #444; margin-bottom: 40mm; }
.cover table { width: auto; margin: 0 auto; font-size: 11.5pt; }
.cover td { border: none; padding: 4px 14px; }
.cover td:first-child { font-weight: 600; text-align: right; color: #12355b; }
"""

JAVA_TOKEN = re.compile(
    r'(?P<comment>//[^\n]*|/\*.*?\*/)'
    r'|(?P<string>"(?:\\.|[^"\\])*")'
    r'|(?P<annotation>@\w+)'
    r'|(?P<keyword>\b(?:public|private|protected|static|final|abstract|class|interface|enum'
    r'|extends|implements|return|new|if|else|for|while|switch|case|default|throw|try|catch'
    r'|void|int|boolean|this|super|null|true|false)\b)',
    re.S)
LIST_ITEM = re.compile(r'^(\s*)([-*]|\d+\.) (.*)$')
IMAGE = re.compile(r'^!\[([^\]]*)\]\(([^)]+)\)$')
LINK = re.compile(r'\[([^\]]+)\]\(([^)\s]+)\)')
CAPTION = re.compile(r'^\*(Figure .+)\*$')
SCREENSHOT = re.compile(r'screenshots/([\w.-]+\.png)')

missing_screenshots = []


def main():
    lines = SOURCE.read_text(encoding="utf-8").splitlines()
    cover_end = lines.index("---")
    page = (
        '<!doctype html><html><head><meta charset="utf-8">'
        "<title>Restaurant Ordering System - Final Project Report</title>"
        f"<style>{CSS}</style></head><body>"
        f'<section class="cover">{render(lines[:cover_end])}</section>'
        f"{render(lines[cover_end + 1:])}</body></html>"
    )
    with tempfile.TemporaryDirectory() as work:
        page_file = Path(work) / "report.html"
        page_file.write_text(page, encoding="utf-8")
        print_to_pdf(page_file, Path(work) / "profile")
    print(f"Written {TARGET}")
    for name in missing_screenshots:
        print(f"  missing screenshot: docs/screenshots/{name}")


def print_to_pdf(page_file, profile_dir):
    browser = find_browser()
    if TARGET.exists():
        TARGET.unlink()
    subprocess.run(
        [browser, "--headless=new", "--disable-gpu", "--no-pdf-header-footer",
         f"--user-data-dir={profile_dir}", f"--print-to-pdf={TARGET}", page_file.as_uri()],
        check=True, timeout=180, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    if not TARGET.exists():
        sys.exit("The browser did not produce a PDF")


def find_browser():
    for candidate in BROWSERS:
        path = candidate if os.path.isabs(candidate) else shutil.which(candidate)
        if path and os.path.exists(path):
            return path
    sys.exit("Chrome or Edge was not found; install one of them to build the PDF")


def render(lines):
    out = []
    i = 0
    while i < len(lines):
        line = lines[i]
        if not line.strip():
            i += 1
        elif line.startswith("```"):
            i = render_code(lines, i, out)
        elif line.strip() == "---":
            # A landscape figure already ends its own page
            if not (out and out[-1].startswith('<figure class="wide"')):
                out.append('<div class="page-break"></div>')
            i += 1
        elif re.match(r"#{1,6} ", line):
            level = len(line) - len(line.lstrip("#"))
            text = line[level:].strip()
            out.append(f'<h{level} id="{slug(text)}">{inline(text)}</h{level}>')
            i += 1
        elif line.startswith("|"):
            i = render_table(lines, i, out)
        elif line.startswith(">"):
            i = render_quote(lines, i, out)
        elif IMAGE.match(line):
            i = render_figure(lines, i, out)
        elif LIST_ITEM.match(line):
            html_list, i = render_list(lines, i, len(LIST_ITEM.match(line).group(1)))
            out.append(html_list)
        else:
            i = render_paragraph(lines, i, out)
    return "\n".join(out)


def render_code(lines, i, out):
    language = lines[i][3:].strip()
    end = i + 1
    while not lines[end].startswith("```"):
        end += 1
    code = "\n".join(lines[i + 1:end])
    if language == "java":
        out.append(f"<pre><code>{highlight_java(code)}</code></pre>")
    else:
        out.append(f'<pre class="console"><code>{html.escape(code)}</code></pre>')
    return end + 1


def render_table(lines, i, out):
    rows = []
    while i < len(lines) and lines[i].startswith("|"):
        rows.append([cell.strip() for cell in lines[i].strip().strip("|").split("|")])
        i += 1
    header, body = rows[0], rows[2:]
    table = ["<table>"]
    if any(header):
        table.append("<thead><tr>" + "".join(f"<th>{inline(c)}</th>" for c in header) + "</tr></thead>")
    for row in body:
        table.append("<tr>" + "".join(f"<td>{inline(c)}</td>" for c in row) + "</tr>")
    table.append("</table>")
    out.append("".join(table))
    return i


def render_quote(lines, i, out):
    text = []
    while i < len(lines) and lines[i].startswith(">"):
        text.append(lines[i].lstrip("> ").strip())
        i += 1
    joined = " ".join(text)
    shot = SCREENSHOT.search(joined)
    if not shot:
        out.append(f"<blockquote><p>{inline(joined)}</p></blockquote>")
    elif (SCREENSHOTS / shot.group(1)).exists():
        out.append(f'<figure><img src="{(SCREENSHOTS / shot.group(1)).as_uri()}"></figure>')
    else:
        missing_screenshots.append(shot.group(1))
        out.append(f'<div class="placeholder">Screenshot placeholder: save the image as '
                   f"<code>docs/screenshots/{shot.group(1)}</code> and rebuild the PDF</div>")
    return i


def render_figure(lines, i, out):
    alt, src = IMAGE.match(lines[i]).groups()
    image = (DOCS / src).resolve()
    vector = image.with_suffix(".svg")
    if vector.exists():
        # Vector diagrams stay sharp when the PDF is zoomed or printed
        image = vector
    width, height = image_size(image)
    css_class = ' class="wide"' if width > 2 * height else ""
    i += 1
    while i < len(lines) and not lines[i].strip():
        i += 1
    caption = ""
    if i < len(lines) and CAPTION.match(lines[i]):
        caption = f"<figcaption>{inline(CAPTION.match(lines[i]).group(1))}</figcaption>"
        i += 1
    out.append(f'<figure{css_class}><img src="{image.as_uri()}" alt="{html.escape(alt)}">{caption}</figure>')
    return i


def render_list(lines, i, indent):
    ordered = LIST_ITEM.match(lines[i]).group(2)[0].isdigit()
    items = []
    while i < len(lines):
        match = LIST_ITEM.match(lines[i])
        if not match or len(match.group(1)) != indent:
            break
        text, nested = match.group(3), ""
        i += 1
        while i < len(lines) and lines[i].strip():
            child = LIST_ITEM.match(lines[i])
            leading = len(lines[i]) - len(lines[i].lstrip())
            if child and len(child.group(1)) > indent:
                sub_list, i = render_list(lines, i, len(child.group(1)))
                nested += sub_list
            elif not child and leading > indent:
                text += " " + lines[i].strip()
                i += 1
            else:
                break
        items.append(f"<li>{inline(text)}{nested}</li>")
    tag = "ol" if ordered else "ul"
    return f"<{tag}>{''.join(items)}</{tag}>", i


def render_paragraph(lines, i, out):
    text = []
    while i < len(lines) and lines[i].strip() and not starts_block(lines[i]):
        text.append(lines[i].strip())
        i += 1
    joined = " ".join(text)
    caption = CAPTION.match(joined)
    if caption:
        out.append(f'<p class="caption">{inline(caption.group(1))}</p>')
    else:
        out.append(f"<p>{inline(joined)}</p>")
    return i


def starts_block(line):
    return (line.startswith(("```", "#", "|", ">", "![")) or line.strip() == "---"
            or LIST_ITEM.match(line) is not None)


def inline(text):
    out, position = [], 0
    for match in LINK.finditer(text):
        out.append(inline_text(text[position:match.start()]))
        out.append(f'<a href="{link(match.group(2))}">{inline_text(match.group(1))}</a>')
        position = match.end()
    out.append(inline_text(text[position:]))
    return "".join(out)


def inline_text(text):
    out = []
    for piece in re.split(r"(`[^`]+`)", text):
        if len(piece) > 1 and piece.startswith("`") and piece.endswith("`"):
            out.append(f"<code>{html.escape(piece[1:-1])}</code>")
            continue
        s = html.escape(piece, quote=False)
        s = re.sub(r"(https?://[^\s<)]+)", r'<a href="\1">\1</a>', s)
        s = re.sub(r"\*\*(.+?)\*\*", r"<strong>\1</strong>", s)
        s = re.sub(r"(?<![\w*])\*(?![\s*])(.+?)(?<![\s*])\*(?![\w*])", r"<em>\1</em>", s)
        s = re.sub(r"(?<!\w)_(?!\s)(.+?)(?<!\s)_(?!\w)", r"<em>\1</em>", s)
        out.append(s)
    return "".join(out)


def link(target):
    if target.startswith(("#", "http://", "https://")):
        return target
    return REPO_URL + target


def slug(text):
    return re.sub(r"[^\w\- ]", "", text.strip().lower()).replace(" ", "-")


def highlight_java(code):
    out, position = [], 0
    for match in JAVA_TOKEN.finditer(code):
        out.append(html.escape(code[position:match.start()]))
        out.append(f'<span class="tok-{match.lastgroup}">{html.escape(match.group())}</span>')
        position = match.end()
    out.append(html.escape(code[position:]))
    return "".join(out)


def image_size(path):
    if path.suffix == ".svg":
        view_box = re.search(r'viewBox="[\d.]+ [\d.]+ ([\d.]+) ([\d.]+)"', path.read_text(encoding="utf-8"))
        return float(view_box.group(1)), float(view_box.group(2))
    with open(path, "rb") as image:
        header = image.read(24)
    return struct.unpack(">II", header[16:24])


if __name__ == "__main__":
    main()
